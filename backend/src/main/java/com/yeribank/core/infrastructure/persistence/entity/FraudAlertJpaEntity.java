package com.yeribank.core.infrastructure.persistence.entity;

import com.yeribank.core.domain.model.enums.FraudAlertSeverity;
import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fraud_alert")
public class FraudAlertJpaEntity {

  @Id
  private UUID id;

  @Column(name = "transfer_id", nullable = false)
  private UUID transferId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Column(name = "rule_code", nullable = false, length = 80)
  private String ruleCode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private FraudAlertSeverity severity;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private FraudAlertStatus status;

  @Column(name = "details_json", nullable = false, length = 4000)
  private String detailsJson;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "reviewed_at")
  private LocalDateTime reviewedAt;

  @Column(name = "reviewed_by")
  private UUID reviewedBy;

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

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public String getRuleCode() {
    return ruleCode;
  }

  public void setRuleCode(String ruleCode) {
    this.ruleCode = ruleCode;
  }

  public FraudAlertSeverity getSeverity() {
    return severity;
  }

  public void setSeverity(FraudAlertSeverity severity) {
    this.severity = severity;
  }

  public FraudAlertStatus getStatus() {
    return status;
  }

  public void setStatus(FraudAlertStatus status) {
    this.status = status;
  }

  public String getDetailsJson() {
    return detailsJson;
  }

  public void setDetailsJson(String detailsJson) {
    this.detailsJson = detailsJson;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getReviewedAt() {
    return reviewedAt;
  }

  public void setReviewedAt(LocalDateTime reviewedAt) {
    this.reviewedAt = reviewedAt;
  }

  public UUID getReviewedBy() {
    return reviewedBy;
  }

  public void setReviewedBy(UUID reviewedBy) {
    this.reviewedBy = reviewedBy;
  }
}
