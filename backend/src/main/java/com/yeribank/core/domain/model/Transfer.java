package com.yeribank.core.domain.model;

import com.yeribank.core.domain.model.enums.TransferStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transfer(
    UUID id,
    UUID fromAccountId,
    UUID toAccountId,
    BigDecimal amount,
    TransferStatus status,
    Integer riskScore,
    LocalDateTime createdAt) {

  public static Transfer create(UUID id, UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
    return new Transfer(
        id,
        fromAccountId,
        toAccountId,
        amount.setScale(4, java.math.RoundingMode.HALF_UP),
        TransferStatus.COMPLETED,
        null,
        LocalDateTime.now());
  }
}
