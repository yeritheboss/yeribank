package com.yeribank.core.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefreshToken(
    UUID id,
    UUID userId,
    String token,
    LocalDateTime expiresAt,
    boolean revoked,
    LocalDateTime createdAt) {

  public boolean isExpired(LocalDateTime now) {
    return expiresAt.isBefore(now);
  }

  public RefreshToken revoke() {
    return new RefreshToken(id, userId, token, expiresAt, true, createdAt);
  }
}
