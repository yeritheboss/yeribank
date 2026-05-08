package com.yeribank.core.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "financial_profile")
public class FinancialProfileJpaEntity {

  @Id
  private UUID id;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Column(name = "monthly_income", nullable = false, precision = 19, scale = 4)
  private BigDecimal monthlyIncome;

  @Column(name = "monthly_expenses", nullable = false, precision = 19, scale = 4)
  private BigDecimal monthlyExpenses;

  @Column(name = "current_debt", nullable = false, precision = 19, scale = 4)
  private BigDecimal currentDebt;

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

  public BigDecimal getMonthlyIncome() {
    return monthlyIncome;
  }

  public void setMonthlyIncome(BigDecimal monthlyIncome) {
    this.monthlyIncome = monthlyIncome;
  }

  public BigDecimal getMonthlyExpenses() {
    return monthlyExpenses;
  }

  public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
    this.monthlyExpenses = monthlyExpenses;
  }

  public BigDecimal getCurrentDebt() {
    return currentDebt;
  }

  public void setCurrentDebt(BigDecimal currentDebt) {
    this.currentDebt = currentDebt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
