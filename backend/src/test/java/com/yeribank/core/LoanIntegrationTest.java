package com.yeribank.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yeribank.core.domain.model.enums.Role;
import com.yeribank.core.infrastructure.web.dto.AuthTokensResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class LoanIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldUpsertFinancialProfileAndApproveHealthyLoanSimulation() throws Exception {
    registerUser("loan-user@yeribank.com", "Secret123", Role.USER);
    AuthTokensResponse userTokens = login("loan-user@yeribank.com", "Secret123");

    mockMvc
        .perform(
            post("/financial-profile")
                .header("Authorization", bearer(userTokens.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "monthlyIncome": 4000.00,
                      "monthlyExpenses": 1500.00,
                      "currentDebt": 500.00
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.monthlyIncome").value(4000.0000))
        .andExpect(jsonPath("$.monthlyExpenses").value(1500.0000))
        .andExpect(jsonPath("$.currentDebt").value(500.0000));

    mockMvc
        .perform(
            post("/loans/simulations")
                .header("Authorization", bearer(userTokens.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "requestedAmount": 12000.00,
                      "termMonths": 24
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("APPROVED"))
        .andExpect(jsonPath("$.score").value(740))
        .andExpect(jsonPath("$.approvedAmount").value(12000.0000));
  }

  @Test
  void shouldPersistLoanApplicationAndListItForOwner() throws Exception {
    registerUser("loan-app@yeribank.com", "Secret123", Role.USER);
    AuthTokensResponse userTokens = login("loan-app@yeribank.com", "Secret123");

    mockMvc
        .perform(
            post("/financial-profile")
                .header("Authorization", bearer(userTokens.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "monthlyIncome": 4000.00,
                      "monthlyExpenses": 1500.00,
                      "currentDebt": 500.00
                    }
                    """))
        .andExpect(status().isOk());

    MvcResult applicationResult =
        mockMvc
            .perform(
                post("/loans/applications")
                    .header("Authorization", bearer(userTokens.accessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "requestedAmount": 12000.00,
                          "termMonths": 24
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("APPROVED"))
            .andReturn();

    Map<String, Object> created =
        objectMapper.readValue(
            applicationResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<Map<String, Object>>() {});
    assertThat(created.get("score")).isEqualTo(740);

    MvcResult listResult =
        mockMvc
            .perform(get("/loans/applications").header("Authorization", bearer(userTokens.accessToken())))
            .andExpect(status().isOk())
            .andReturn();

    Map<String, Object> page =
        objectMapper.readValue(
            listResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<Map<String, Object>>() {});
    List<Map<String, Object>> applications = (List<Map<String, Object>>) page.get("content");
    assertThat(applications).hasSize(1);
    assertThat(applications.get(0).get("status")).isEqualTo("APPROVED");
    assertThat(applications.get(0).get("score")).isEqualTo(740);
  }
}
