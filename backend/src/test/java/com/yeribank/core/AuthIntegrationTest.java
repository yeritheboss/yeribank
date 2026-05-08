package com.yeribank.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yeribank.core.domain.model.enums.Role;
import com.yeribank.core.infrastructure.web.dto.AuthTokensResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthIntegrationTest extends AbstractIntegrationTest {

  @Test
  void shouldRegisterLoginAndRefreshTokens() throws Exception {
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "admin@yeribank.com",
                      "password": "Secret123",
                      "role": "ADMIN",
                      "fullName": "Admin Yeri",
                      "age": 35,
                      "jobTitle": "Bank manager"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("admin@yeribank.com"))
        .andExpect(jsonPath("$.role").value("ADMIN"))
        .andExpect(jsonPath("$.fullName").value("Admin Yeri"))
        .andExpect(jsonPath("$.age").value(35))
        .andExpect(jsonPath("$.jobTitle").value("Bank manager"));

    AuthTokensResponse loginTokens = login("admin@yeribank.com", "Secret123");

    assertThat(loginTokens.accessToken()).isNotBlank();
    assertThat(loginTokens.refreshToken()).isNotBlank();
    assertThat(loginTokens.tokenType()).isEqualTo("Bearer");

    AuthTokensResponse refreshTokens = refresh(loginTokens.refreshToken());

    assertThat(refreshTokens.accessToken()).isNotBlank();
    assertThat(refreshTokens.refreshToken()).isNotBlank();
    assertThat(refreshTokens.refreshToken()).isNotEqualTo(loginTokens.refreshToken());

    mockMvc
        .perform(get("/users").header("Authorization", bearer(loginTokens.accessToken())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].email").value("admin@yeribank.com"))
        .andExpect(jsonPath("$.content[0].fullName").value("Admin Yeri"))
        .andExpect(jsonPath("$.totalElements").value(1));

    mockMvc
        .perform(get("/audit-logs").header("Authorization", bearer(loginTokens.accessToken())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].action").exists());
  }

  @Test
  void shouldRejectLoginWithWrongPassword() throws Exception {
    registerUser("user@yeribank.com", "Secret123", Role.USER);

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "user@yeribank.com",
                      "password": "wrong-pass"
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid credentials"));
  }

  @Test
  void shouldForbidAuditLogsForRegularUser() throws Exception {
    registerUser("regular@yeribank.com", "Secret123", Role.USER);
    AuthTokensResponse tokens = login("regular@yeribank.com", "Secret123");

    mockMvc
        .perform(get("/audit-logs").header("Authorization", bearer(tokens.accessToken())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Only ADMIN can view audit logs"));
  }
}
