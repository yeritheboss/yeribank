package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.FinancialProfileJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialProfileJpaRepository extends JpaRepository<FinancialProfileJpaEntity, UUID> {
  Optional<FinancialProfileJpaEntity> findByUserId(UUID userId);
}
