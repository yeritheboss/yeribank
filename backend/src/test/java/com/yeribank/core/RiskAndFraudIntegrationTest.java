package com.yeribank.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yeribank.core.domain.model.enums.Role;
import com.yeribank.core.infrastructure.web.dto.AuthTokensResponse;
import com.yeribank.core.infrastructure.web.dto.UserResponse;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class RiskAndFraudIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldCreateRiskAssessmentProfileAndAlertsForSuspiciousTransfer() throws Exception {
    registerUser("alice-risk@yeribank.com", "Secret123", Role.USER);
    registerUser("bob-risk@yeribank.com", "Secret123", Role.USER);

    AuthTokensResponse aliceTokens = login("alice-risk@yeribank.com", "Secret123");
    AuthTokensResponse bobTokens = login("bob-risk@yeribank.com", "Secret123");

    UUID fromAccountId =
        UUID.fromString(
            createAccount(aliceTokens.accessToken(), null, new BigDecimal("5000.00")).get("id").toString());
    UUID toAccountId =
        UUID.fromString(
            createAccount(bobTokens.accessToken(), null, new BigDecimal("100.00")).get("id").toString());

    MvcResult transferResult =
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
                          "amount": 2500.00
                        }
                        """
                            .formatted(fromAccountId, toAccountId)))
            .andExpect(status().isCreated())
            .andReturn();

    Map<String, Object> transferResponse =
        objectMapper.readValue(
            transferResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<Map<String, Object>>() {});
    UUID transferId = UUID.fromString(transferResponse.get("id").toString());

    mockMvc
        .perform(get("/risk/profile/me").header("Authorization", bearer(aliceTokens.accessToken())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.score").value(500))
        .andExpect(jsonPath("$.riskLevel").value("HIGH"))
        .andExpect(jsonPath("$.alertCount90d").value(2));

    mockMvc
        .perform(
            get("/risk/assessments/transfers/{transferId}", transferId)
                .header("Authorization", bearer(aliceTokens.accessToken())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.score").value(500))
        .andExpect(jsonPath("$.decision").value("REVIEW"))
        .andExpect(jsonPath("$.reasons[0]").value("BALANCE_DRAIN_40P"))
        .andExpect(jsonPath("$.reasons[1]").value("FIRST_HIGH_VALUE_COUNTERPARTY"));

    MvcResult alertsResult =
        mockMvc
            .perform(get("/fraud/alerts").header("Authorization", bearer(aliceTokens.accessToken())))
            .andExpect(status().isOk())
            .andReturn();

    Map<String, Object> alertsPage =
        objectMapper.readValue(
            alertsResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<Map<String, Object>>() {});
    List<Map<String, Object>> alerts = (List<Map<String, Object>>) alertsPage.get("content");
    assertThat(alerts).hasSize(2);
    assertThat(alerts).extracting(alert -> alert.get("ruleCode"))
        .contains("FIRST_HIGH_VALUE_COUNTERPARTY", "BALANCE_DRAIN_40P");
  }

  @Test
  void shouldAllowAdminToReviewFraudAlert() throws Exception {
    UserResponse admin = registerUser("admin-fraud@yeribank.com", "Secret123", Role.ADMIN);
    registerUser("alice-fraud@yeribank.com", "Secret123", Role.USER);
    registerUser("bob-fraud@yeribank.com", "Secret123", Role.USER);

    AuthTokensResponse adminTokens = login("admin-fraud@yeribank.com", "Secret123");
    AuthTokensResponse aliceTokens = login("alice-fraud@yeribank.com", "Secret123");
    AuthTokensResponse bobTokens = login("bob-fraud@yeribank.com", "Secret123");

    UUID fromAccountId =
        UUID.fromString(
            createAccount(aliceTokens.accessToken(), null, new BigDecimal("5000.00")).get("id").toString());
    UUID toAccountId =
        UUID.fromString(
            createAccount(bobTokens.accessToken(), null, new BigDecimal("100.00")).get("id").toString());

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
                      "amount": 2500.00
                    }
                    """
                        .formatted(fromAccountId, toAccountId)))
        .andExpect(status().isCreated());

    MvcResult alertsResult =
        mockMvc
            .perform(get("/fraud/alerts").header("Authorization", bearer(adminTokens.accessToken())))
            .andExpect(status().isOk())
            .andReturn();

    Map<String, Object> alertsPage =
        objectMapper.readValue(
            alertsResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<Map<String, Object>>() {});
    List<Map<String, Object>> alerts = (List<Map<String, Object>>) alertsPage.get("content");
    UUID alertId = UUID.fromString(alerts.get(0).get("id").toString());

    mockMvc
        .perform(
            patch("/fraud/alerts/{alertId}/status", alertId)
                .header("Authorization", bearer(adminTokens.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "status": "REVIEWED"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REVIEWED"))
        .andExpect(jsonPath("$.reviewedBy").value(admin.id().toString()));
  }
}
