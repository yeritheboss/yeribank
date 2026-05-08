package com.yeribank.core.infrastructure.persistence.entity;

import com.yeribank.core.domain.model.enums.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "risk_profile")
public class RiskProfileJpaEntity {

  @Id
  private UUID id;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Column(nullable = false)
  private int score;

  @Column(name = "last_assessment_score", nullable = false)
  private int lastAssessmentScore;

  @Enumerated(EnumType.STRING)
  @Column(name = "risk_level", nullable = false, length = 20)
  private RiskLevel riskLevel;

  @Column(name = "alert_count_90d", nullable = false)
  private long alertCount90d;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getLastAssessmentScore() {
    return lastAssessmentScore;
  }

  public void setLastAssessmentScore(int lastAssessmentScore) {
    this.lastAssessmentScore = lastAssessmentScore;
  }

  public RiskLevel getRiskLevel() {
    return riskLevel;
  }

  public void setRiskLevel(RiskLevel riskLevel) {
    this.riskLevel = riskLevel;
  }

  public long getAlertCount90d() {
    return alertCount90d;
  }

  public void setAlertCount90d(long alertCount90d) {
    this.alertCount90d = alertCount90d;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
