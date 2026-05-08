package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.Transfer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepositoryPort {
  Transfer save(Transfer transfer);

  Optional<Transfer> findById(UUID id);

  List<Transfer> findByAccountId(UUID accountId, int limit);

  List<Transfer> findByAccountIds(List<UUID> accountIds, int limit);

  void updateRiskScore(UUID transferId, int riskScore);

  long countByFromAccountIdAndCreatedAtAfter(UUID fromAccountId, LocalDateTime threshold);

  long countByFromAccountIdAndToAccountId(UUID fromAccountId, UUID toAccountId);

  BigDecimal averageAmountByFromAccountIdAndCreatedAtAfter(UUID fromAccountId, LocalDateTime threshold);
}
