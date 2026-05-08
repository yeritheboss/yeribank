package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.TransferRiskAssessmentJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRiskAssessmentJpaRepository
    extends JpaRepository<TransferRiskAssessmentJpaEntity, UUID> {
  Optional<TransferRiskAssessmentJpaEntity> findByTransferId(UUID transferId);
}
