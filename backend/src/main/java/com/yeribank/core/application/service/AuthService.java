package com.yeribank.core.application.service;

import com.yeribank.core.application.dto.AuthTokensResult;
import com.yeribank.core.application.dto.LoginCommand;
import com.yeribank.core.application.dto.RefreshCommand;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.AuthUseCase;
import com.yeribank.core.application.port.out.RefreshTokenRepositoryPort;
import com.yeribank.core.application.port.out.TokenProviderPort;
import com.yeribank.core.application.port.out.UserRepositoryPort;
import com.yeribank.core.domain.model.RefreshToken;
import com.yeribank.core.domain.model.User;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements AuthUseCase {

  private final UserRepositoryPort userRepository;
  private final RefreshTokenRepositoryPort refreshTokenRepository;
  private final TokenProviderPort tokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final AuditLogService auditLogService;
  private final Clock clock;
  private final long refreshTokenDays;

  public AuthService(
      UserRepositoryPort userRepository,
      RefreshTokenRepositoryPort refreshTokenRepository,
      TokenProviderPort tokenProvider,
      PasswordEncoder passwordEncoder,
      AuditLogService auditLogService,
      Clock clock,
      @Value("${security.jwt.refresh-token-days}") long refreshTokenDays) {
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.tokenProvider = tokenProvider;
    this.passwordEncoder = passwordEncoder;
    this.auditLogService = auditLogService;
    this.clock = clock;
    this.refreshTokenDays = refreshTokenDays;
  }

  @Override
  @Transactional
  public AuthTokensResult login(LoginCommand command) {
    String normalizedEmail = command.email().trim().toLowerCase();
    User user =
        userRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(
                () -> {
                  auditLogService.record(
                      null,
                      "LOGIN_FAILED",
                      "USER",
                      normalizedEmail,
                      "FAILURE",
                      "{\"reason\":\"USER_NOT_FOUND\"}");
                  return new AppException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                });

    if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
      auditLogService.record(
          user.id(),
          "LOGIN_FAILED",
          "USER",
          user.id().toString(),
          "FAILURE",
          "{\"reason\":\"INVALID_PASSWORD\"}");
      throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    AuthTokensResult tokens = issueTokens(user);
    auditLogService.record(
        user.id(),
        "LOGIN_SUCCEEDED",
        "USER",
        user.id().toString(),
        "SUCCESS",
        "{\"email\":\"" + user.email() + "\"}");
    return tokens;
  }

  @Override
  @Transactional
  public AuthTokensResult refresh(RefreshCommand command) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(command.refreshToken())
            .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

    LocalDateTime now = LocalDateTime.now(clock);
    if (refreshToken.revoked() || refreshToken.isExpired(now)) {
      throw new AppException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
    }

    refreshTokenRepository.save(refreshToken.revoke());

    User user =
        userRepository
            .findById(refreshToken.userId())
            .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "User no longer exists"));

    AuthTokensResult tokens = issueTokens(user);
    auditLogService.record(
        user.id(),
        "TOKEN_REFRESHED",
        "USER",
        user.id().toString(),
        "SUCCESS",
        "{\"refreshTokenId\":\"" + refreshToken.id() + "\"}");
    return tokens;
  }

  private AuthTokensResult issueTokens(User user) {
    LocalDateTime now = LocalDateTime.now(clock);
    RefreshToken refreshToken =
        new RefreshToken(
            UUID.randomUUID(),
            user.id(),
            UUID.randomUUID().toString(),
            now.plusDays(refreshTokenDays),
            false,
            now);

    refreshTokenRepository.save(refreshToken);

    return new AuthTokensResult(
        tokenProvider.generateAccessToken(user),
        refreshToken.token(),
        "Bearer",
        tokenProvider.getAccessTokenExpirationSeconds());
  }
}
