package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {
  Optional<RefreshTokenJpaEntity> findByToken(String token);
}
