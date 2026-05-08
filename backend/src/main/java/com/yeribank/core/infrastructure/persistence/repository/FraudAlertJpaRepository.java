package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import com.yeribank.core.infrastructure.persistence.entity.FraudAlertJpaEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudAlertJpaRepository extends JpaRepository<FraudAlertJpaEntity, UUID> {
  List<FraudAlertJpaEntity> findAllByOrderByCreatedAtDesc();

  List<FraudAlertJpaEntity> findByStatusOrderByCreatedAtDesc(FraudAlertStatus status);

  List<FraudAlertJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

  List<FraudAlertJpaEntity> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, FraudAlertStatus status);

  long countByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime threshold);
}
