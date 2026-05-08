package com.yeribank.core;

import com.yeribank.core.application.port.out.TransferEventPublisherPort;
import com.yeribank.core.domain.model.enums.Role;
import com.yeribank.core.infrastructure.persistence.repository.AccountJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.AuditLogJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.FinancialProfileJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.FraudAlertJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.LoanApplicationJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.RiskProfileJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.TransferJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.TransferRiskAssessmentJpaRepository;
import com.yeribank.core.infrastructure.persistence.repository.UserJpaRepository;
import com.yeribank.core.infrastructure.web.dto.AuthTokensResponse;
import com.yeribank.core.infrastructure.web.dto.UserResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired private TransferJpaRepository transferRepository;
  @Autowired private TransferRiskAssessmentJpaRepository transferRiskAssessmentRepository;
  @Autowired private AccountJpaRepository accountRepository;
  @Autowired private LoanApplicationJpaRepository loanApplicationRepository;
  @Autowired private FinancialProfileJpaRepository financialProfileRepository;
  @Autowired private RiskProfileJpaRepository riskProfileRepository;
  @Autowired private FraudAlertJpaRepository fraudAlertRepository;
  @Autowired private RefreshTokenJpaRepository refreshTokenRepository;
  @Autowired private UserJpaRepository userRepository;
  @Autowired private AuditLogJpaRepository auditLogRepository;

  @MockBean protected TransferEventPublisherPort transferEventPublisherPort;

  @BeforeEach
  void cleanDatabase() {
    loanApplicationRepository.deleteAll();
    financialProfileRepository.deleteAll();
    fraudAlertRepository.deleteAll();
    transferRiskAssessmentRepository.deleteAll();
    riskProfileRepository.deleteAll();
    transferRepository.deleteAll();
    refreshTokenRepository.deleteAll();
    auditLogRepository.deleteAll();
    accountRepository.deleteAll();
    userRepository.deleteAll();
  }

  protected UserResponse registerUser(String email, String password, Role role) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            Map.of("email", email, "password", password, "role", role.name()))))
            .andReturn();

    return objectMapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8), UserResponse.class);
  }

  protected AuthTokensResponse login(String email, String password) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            Map.of("email", email, "password", password))))
            .andReturn();

    return objectMapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8), AuthTokensResponse.class);
  }

  protected AuthTokensResponse refresh(String refreshToken) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            Map.of("refreshToken", refreshToken))))
            .andReturn();

    return objectMapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8), AuthTokensResponse.class);
  }

  protected Map<String, Object> createAccount(String token, UUID userId, BigDecimal initialBalance)
      throws Exception {
    Map<String, Object> payload = new java.util.LinkedHashMap<>();
    if (userId != null) {
      payload.put("userId", userId);
    }
    if (initialBalance != null) {
      payload.put("initialBalance", initialBalance);
    }

    MvcResult result =
        mockMvc
            .perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/accounts")
                    .header("Authorization", bearer(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
            .andReturn();

    return objectMapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<Map<String, Object>>() {});
  }

  protected BigDecimal getBalance(String token, UUID accountId) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                        "/accounts/{id}/balance", accountId)
                    .header("Authorization", bearer(token)))
            .andReturn();

    Map<String, Object> response =
        objectMapper.readValue(
            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
            new TypeReference<Map<String, Object>>() {});
    return new BigDecimal(response.get("balance").toString());
  }

  protected String bearer(String token) {
    return "Bearer " + token;
  }
}
