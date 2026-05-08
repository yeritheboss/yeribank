package com.yeribank.core.domain.model;

import com.yeribank.core.domain.model.enums.RiskDecision;
import com.yeribank.core.domain.model.enums.RiskLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferRiskAssessment(
    UUID id,
    UUID transferId,
    UUID userId,
    int score,
    RiskDecision decision,
    RiskLevel riskLevel,
    String reasonsJson,
    LocalDateTime createdAt) {

  public static TransferRiskAssessment create(
      UUID id,
      UUID transferId,
      UUID userId,
      int score,
      String reasonsJson,
      LocalDateTime createdAt) {
    int normalizedScore = Math.max(0, Math.min(1000, score));
    return new TransferRiskAssessment(
        id,
        transferId,
        userId,
        normalizedScore,
        normalizedScore >= 650 ? RiskDecision.ALLOW : RiskDecision.REVIEW,
        RiskProfile.resolveRiskLevel(normalizedScore),
        reasonsJson,
        createdAt);
  }
}
