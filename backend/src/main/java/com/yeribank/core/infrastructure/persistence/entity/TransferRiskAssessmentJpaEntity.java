package com.yeribank.core.infrastructure.persistence.entity;

import com.yeribank.core.domain.model.enums.RiskDecision;
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
@Table(name = "transfer_risk_assessment")
public class TransferRiskAssessmentJpaEntity {

  @Id
  private UUID id;

  @Column(name = "transfer_id", nullable = false, unique = true)
  private UUID transferId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private int score;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RiskDecision decision;

  @Enumerated(EnumType.STRING)
  @Column(name = "risk_level", nullable = false, length = 20)
  private RiskLevel riskLevel;

  @Column(name = "reasons_json", nullable = false, length = 4000)
  private String reasonsJson;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getTransferId() {
    return transferId;
  }

  public void setTransferId(UUID transferId) {
    this.transferId = transferId;
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

  public RiskDecision getDecision() {
    return decision;
  }

  public void setDecision(RiskDecision decision) {
    this.decision = decision;
  }

  public RiskLevel getRiskLevel() {
    return riskLevel;
  }

  public void setRiskLevel(RiskLevel riskLevel) {
    this.riskLevel = riskLevel;
  }

  public String getReasonsJson() {
    return reasonsJson;
  }

  public void setReasonsJson(String reasonsJson) {
    this.reasonsJson = reasonsJson;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
