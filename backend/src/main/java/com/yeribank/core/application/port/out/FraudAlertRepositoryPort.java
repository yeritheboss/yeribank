package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.FraudAlert;
import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FraudAlertRepositoryPort {
  FraudAlert save(FraudAlert alert);

  Optional<FraudAlert> findById(UUID id);

  List<FraudAlert> findAll();

  List<FraudAlert> findByStatus(FraudAlertStatus status);

  List<FraudAlert> findByUserId(UUID userId);

  List<FraudAlert> findByUserIdAndStatus(UUID userId, FraudAlertStatus status);

  long countByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime threshold);
}
