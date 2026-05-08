package com.yeribank.core.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialProfile(
    UUID id,
    UUID userId,
    BigDecimal monthlyIncome,
    BigDecimal monthlyExpenses,
    BigDecimal currentDebt,
    LocalDateTime updatedAt) {

  public static FinancialProfile create(
      UUID id,
      UUID userId,
      BigDecimal monthlyIncome,
      BigDecimal monthlyExpenses,
      BigDecimal currentDebt,
      LocalDateTime updatedAt) {
    return new FinancialProfile(
        id,
        userId,
        normalize(monthlyIncome),
        normalize(monthlyExpenses),
        normalize(currentDebt),
        updatedAt);
  }

  public FinancialProfile update(
      BigDecimal monthlyIncome,
      BigDecimal monthlyExpenses,
      BigDecimal currentDebt,
      LocalDateTime updatedAt) {
    return new FinancialProfile(
        id,
        userId,
        normalize(monthlyIncome),
        normalize(monthlyExpenses),
        normalize(currentDebt),
        updatedAt);
  }

  private static BigDecimal normalize(BigDecimal amount) {
    return amount.setScale(4, RoundingMode.HALF_UP);
  }
}
