package com.yeribank.core.infrastructure.persistence.entity;

import com.yeribank.core.domain.model.enums.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfer")
public class TransferJpaEntity {

  @Id
  private UUID id;

  @Column(name = "from_account_id", nullable = false)
  private UUID fromAccountId;

  @Column(name = "to_account_id", nullable = false)
  private UUID toAccountId;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private TransferStatus status;

  @Column(name = "risk_score")
  private Integer riskScore;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getFromAccountId() {
    return fromAccountId;
  }

  public void setFromAccountId(UUID fromAccountId) {
    this.fromAccountId = fromAccountId;
  }

  public UUID getToAccountId() {
    return toAccountId;
  }

  public void setToAccountId(UUID toAccountId) {
    this.toAccountId = toAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public TransferStatus getStatus() {
    return status;
  }

  public void setStatus(TransferStatus status) {
    this.status = status;
  }

  public Integer getRiskScore() {
    return riskScore;
  }

  public void setRiskScore(Integer riskScore) {
    this.riskScore = riskScore;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
