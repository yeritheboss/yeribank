package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.RefreshTokenRepositoryPort;
import com.yeribank.core.domain.model.RefreshToken;
import com.yeribank.core.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

  private final RefreshTokenJpaRepository repository;

  public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    return toDomain(repository.save(toEntity(refreshToken)));
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return repository.findByToken(token).map(this::toDomain);
  }

  private RefreshTokenJpaEntity toEntity(RefreshToken refreshToken) {
    RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
    entity.setId(refreshToken.id());
    entity.setUserId(refreshToken.userId());
    entity.setToken(refreshToken.token());
    entity.setExpiresAt(refreshToken.expiresAt());
    entity.setRevoked(refreshToken.revoked());
    entity.setCreatedAt(refreshToken.createdAt());
    return entity;
  }

  private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
    return new RefreshToken(
        entity.getId(),
        entity.getUserId(),
        entity.getToken(),
        entity.getExpiresAt(),
        entity.isRevoked(),
        entity.getCreatedAt());
  }
}
