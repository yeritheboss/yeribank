package com.yeribank.core;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yeribank.core.domain.model.enums.Role;
import com.yeribank.core.infrastructure.web.dto.AuthTokensResponse;
import com.yeribank.core.infrastructure.web.dto.UserResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AccountIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldCreateAccountAndReadBalanceAsOwner() throws Exception {
    UserResponse user = registerUser("owner@yeribank.com", "Secret123", Role.USER);
    AuthTokensResponse tokens = login("owner@yeribank.com", "Secret123");

    mockMvc
        .perform(
            post("/accounts")
                .header("Authorization", bearer(tokens.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "initialBalance": 150.25
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(user.id().toString()))
        .andExpect(jsonPath("$.accountNumber").value(org.hamcrest.Matchers.matchesRegex("ES20\\d{20}")))
        .andExpect(jsonPath("$.ownerEmail").value("owner@yeribank.com"))
        .andExpect(jsonPath("$.balance").value(150.2500));
  }

  @Test
  void shouldForbidBalanceAccessFromAnotherUser() throws Exception {
    registerUser("owner@yeribank.com", "Secret123", Role.USER);
    AuthTokensResponse ownerTokens = login("owner@yeribank.com", "Secret123");
    registerUser("outsider@yeribank.com", "Secret123", Role.USER);
    AuthTokensResponse outsiderTokens = login("outsider@yeribank.com", "Secret123");

    UUID accountId =
        UUID.fromString(createAccount(ownerTokens.accessToken(), null, new java.math.BigDecimal("80.00")).get("id").toString());

    mockMvc
        .perform(
            get("/accounts/{id}/balance", accountId)
                .header("Authorization", bearer(outsiderTokens.accessToken())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You cannot access this account"));
  }
}
