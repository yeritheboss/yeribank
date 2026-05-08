package com.yeribank.core.domain.model;

import com.yeribank.core.domain.model.enums.RiskLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public record RiskProfile(
    UUID id,
    UUID userId,
    int score,
    int lastAssessmentScore,
    RiskLevel riskLevel,
    long alertCount90d,
    LocalDateTime updatedAt) {

  public static RiskProfile create(
      UUID id,
      UUID userId,
      int score,
      int lastAssessmentScore,
      long alertCount90d,
      LocalDateTime updatedAt) {
    int normalizedScore = normalize(score);
    int normalizedLastAssessmentScore = normalize(lastAssessmentScore);
    return new RiskProfile(
        id,
        userId,
        normalizedScore,
        normalizedLastAssessmentScore,
        resolveRiskLevel(normalizedScore),
        alertCount90d,
        updatedAt);
  }

  public RiskProfile update(int nextScore, int nextLastAssessmentScore, long nextAlertCount90d, LocalDateTime updatedAt) {
    int normalizedScore = normalize(nextScore);
    int normalizedLastAssessmentScore = normalize(nextLastAssessmentScore);
    return new RiskProfile(
        id,
        userId,
        normalizedScore,
        normalizedLastAssessmentScore,
        resolveRiskLevel(normalizedScore),
        nextAlertCount90d,
        updatedAt);
  }

  private static int normalize(int score) {
    return Math.max(0, Math.min(1000, score));
  }

  public static RiskLevel resolveRiskLevel(int score) {
    if (score >= 750) {
      return RiskLevel.LOW;
    }
    if (score >= 550) {
      return RiskLevel.MEDIUM;
    }
    return RiskLevel.HIGH;
  }
}
