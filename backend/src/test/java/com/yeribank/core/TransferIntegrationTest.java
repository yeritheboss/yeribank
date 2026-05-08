package com.yeribank.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yeribank.core.application.event.TransferCreatedEvent;
import com.yeribank.core.domain.model.enums.Role;
import com.yeribank.core.infrastructure.web.dto.AuthTokensResponse;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class TransferIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldTransferFundsAndPublishEvent() throws Exception {
    registerUser("alice@yeribank.com", "Secret123", Role.USER);
    registerUser("bob@yeribank.com", "Secret123", Role.USER);

    AuthTokensResponse aliceTokens = login("alice@yeribank.com", "Secret123");
    AuthTokensResponse bobTokens = login("bob@yeribank.com", "Secret123");

    Map<String, Object> fromAccount = createAccount(aliceTokens.accessToken(), null, new BigDecimal("200.00"));
    UUID fromAccountId = UUID.fromString(fromAccount.get("id").toString());
    String fromAccountNumber = fromAccount.get("accountNumber").toString();
    Map<String, Object> toAccount = createAccount(bobTokens.accessToken(), null, new BigDecimal("25.00"));
    UUID toAccountId = UUID.fromString(toAccount.get("id").toString());
    String toAccountNumber = toAccount.get("accountNumber").toString();

    mockMvc
        .perform(
            post("/transfers")
                .header("Authorization", bearer(aliceTokens.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "fromAccountNumber": "%s",
                      "toAccountNumber": "%s",
                      "amount": 50.00
                    }
                    """
                        .formatted(fromAccountNumber, toAccountNumber)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fromAccountNumber").value(fromAccountNumber))
        .andExpect(jsonPath("$.toAccountNumber").value(toAccountNumber))
        .andExpect(jsonPath("$.amount").value(50.0000))
        .andExpect(jsonPath("$.status").value("COMPLETED"));

    assertThat(getBalance(aliceTokens.accessToken(), fromAccountId)).isEqualByComparingTo("150.0000");
    assertThat(getBalance(bobTokens.accessToken(), toAccountId)).isEqualByComparingTo("75.0000");
    verify(transferEventPublisherPort, times(1)).publish(any(TransferCreatedEvent.class));

    mockMvc
        .perform(
            get("/accounts/{accountNumber}/transfers", fromAccountNumber)
                .header("Authorization", bearer(aliceTokens.accessToken())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].fromAccountNumber").value(fromAccountNumber))
        .andExpect(jsonPath("$.content[0].toAccountNumber").value(toAccountNumber))
        .andExpect(jsonPath("$.content[0].direction").value("OUTGOING"))
        .andExpect(jsonPath("$.totalElements").value(1));

    mockMvc
        .perform(get("/dashboard/me").header("Authorization", bearer(aliceTokens.accessToken())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.email").value("alice@yeribank.com"))
        .andExpect(jsonPath("$.accounts[0].accountNumber").value(fromAccountNumber))
        .andExpect(jsonPath("$.recentTransfers[0].fromAccountNumber").value(fromAccountNumber));
  }

  @Test
  void shouldRejectTransferWhenBalanceIsInsufficient() throws Exception {
    registerUser("alice@yeribank.com", "Secret123", Role.USER);
    registerUser("bob@yeribank.com", "Secret123", Role.USER);

    AuthTokensResponse aliceTokens = login("alice@yeribank.com", "Secret123");
    AuthTokensResponse bobTokens = login("bob@yeribank.com", "Secret123");

    UUID fromAccountId =
        UUID.fromString(createAccount(aliceTokens.accessToken(), null, new BigDecimal("20.00")).get("id").toString());
    UUID toAccountId =
        UUID.fromString(createAccount(bobTokens.accessToken(), null, new BigDecimal("10.00")).get("id").toString());

    mockMvc
        .perform(
            post("/transfers")
                .header("Authorization", bearer(aliceTokens.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "fromAccountId": "%s",
                      "toAccountId": "%s",
                      "amount": 50.00
                    }
                    """
                        .formatted(fromAccountId, toAccountId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Insufficient balance"));
  }
}
