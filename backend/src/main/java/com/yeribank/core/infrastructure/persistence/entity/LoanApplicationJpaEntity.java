package com.yeribank.core.infrastructure.persistence.entity;

import com.yeribank.core.domain.model.enums.LoanApplicationStatus;
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
@Table(name = "loan_application")
public class LoanApplicationJpaEntity {

  @Id
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "requested_amount", nullable = false, precision = 19, scale = 4)
  private BigDecimal requestedAmount;

  @Column(name = "approved_amount", nullable = false, precision = 19, scale = 4)
  private BigDecimal approvedAmount;

  @Column(name = "term_months", nullable = false)
  private int termMonths;

  @Column(name = "annual_interest_rate", nullable = false, precision = 8, scale = 4)
  private BigDecimal annualInterestRate;

  @Column(name = "estimated_installment", nullable = false, precision = 19, scale = 4)
  private BigDecimal estimatedInstallment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LoanApplicationStatus status;

  @Column(name = "risk_snapshot_json", nullable = false, length = 4000)
  private String riskSnapshotJson;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

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

  public BigDecimal getRequestedAmount() {
    return requestedAmount;
  }

  public void setRequestedAmount(BigDecimal requestedAmount) {
    this.requestedAmount = requestedAmount;
  }

  public BigDecimal getApprovedAmount() {
    return approvedAmount;
  }

  public void setApprovedAmount(BigDecimal approvedAmount) {
    this.approvedAmount = approvedAmount;
  }

  public int getTermMonths() {
    return termMonths;
  }

  public void setTermMonths(int termMonths) {
    this.termMonths = termMonths;
  }

  public BigDecimal getAnnualInterestRate() {
    return annualInterestRate;
  }

  public void setAnnualInterestRate(BigDecimal annualInterestRate) {
    this.annualInterestRate = annualInterestRate;
  }

  public BigDecimal getEstimatedInstallment() {
    return estimatedInstallment;
  }

  public void setEstimatedInstallment(BigDecimal estimatedInstallment) {
    this.estimatedInstallment = estimatedInstallment;
  }

  public LoanApplicationStatus getStatus() {
    return status;
  }

  public void setStatus(LoanApplicationStatus status) {
    this.status = status;
  }

  public String getRiskSnapshotJson() {
    return riskSnapshotJson;
  }

  public void setRiskSnapshotJson(String riskSnapshotJson) {
    this.riskSnapshotJson = riskSnapshotJson;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
