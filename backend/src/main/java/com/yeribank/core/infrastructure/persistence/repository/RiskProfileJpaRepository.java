package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.RiskProfileJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskProfileJpaRepository extends JpaRepository<RiskProfileJpaEntity, UUID> {
  Optional<RiskProfileJpaEntity> findByUserId(UUID userId);
}
