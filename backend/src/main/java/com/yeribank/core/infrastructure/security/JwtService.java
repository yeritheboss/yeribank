package com.yeribank.core.infrastructure.security;

import com.yeribank.core.application.port.out.TokenProviderPort;
import com.yeribank.core.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService implements TokenProviderPort {

  private final SecretKey key;
  private final long accessTokenMinutes;

  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.access-token-minutes}") long accessTokenMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenMinutes = accessTokenMinutes;
  }

  @Override
  public String generateAccessToken(User user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(getAccessTokenExpirationSeconds());

    return Jwts.builder()
        .subject(user.id().toString())
        .claim("email", user.email())
        .claim("role", user.role().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(key)
        .compact();
  }

  @Override
  public long getAccessTokenExpirationSeconds() {
    return accessTokenMinutes * 60;
  }

  public Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  public UUID extractUserId(String token) {
    return UUID.fromString(parseClaims(token).getSubject());
  }

  public String extractRole(String token) {
    Object role = parseClaims(token).get("role");
    return role == null ? "USER" : role.toString();
  }
}
