package com.yeribank.core.domain.model;

import com.yeribank.core.domain.model.enums.FraudAlertSeverity;
import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record FraudAlert(
    UUID id,
    UUID transferId,
    UUID userId,
    UUID accountId,
    String ruleCode,
    FraudAlertSeverity severity,
    FraudAlertStatus status,
    String detailsJson,
    LocalDateTime createdAt,
    LocalDateTime reviewedAt,
    UUID reviewedBy) {

  public static FraudAlert create(
      UUID id,
      UUID transferId,
      UUID userId,
      UUID accountId,
      String ruleCode,
      FraudAlertSeverity severity,
      String detailsJson,
      LocalDateTime createdAt) {
    return new FraudAlert(
        id,
        transferId,
        userId,
        accountId,
        ruleCode,
        severity,
        FraudAlertStatus.OPEN,
        detailsJson,
        createdAt,
        null,
        null);
  }

  public FraudAlert review(FraudAlertStatus nextStatus, UUID reviewerId, LocalDateTime reviewedAt) {
    return new FraudAlert(
        id,
        transferId,
        userId,
        accountId,
        ruleCode,
        severity,
        nextStatus,
        detailsJson,
        createdAt,
        reviewedAt,
        reviewerId);
  }
}
