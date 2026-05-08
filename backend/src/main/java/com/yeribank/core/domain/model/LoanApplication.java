package com.yeribank.core.domain.model;

import com.yeribank.core.domain.model.enums.LoanApplicationStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanApplication(
    UUID id,
    UUID userId,
    BigDecimal requestedAmount,
    BigDecimal approvedAmount,
    int termMonths,
    BigDecimal annualInterestRate,
    BigDecimal estimatedInstallment,
    LoanApplicationStatus status,
    String riskSnapshotJson,
    LocalDateTime createdAt) {

  public static LoanApplication create(
      UUID id,
      UUID userId,
      BigDecimal requestedAmount,
      BigDecimal approvedAmount,
      int termMonths,
      BigDecimal annualInterestRate,
      BigDecimal estimatedInstallment,
      LoanApplicationStatus status,
      String riskSnapshotJson,
      LocalDateTime createdAt) {
    return new LoanApplication(
        id,
        userId,
        normalize(requestedAmount),
        normalize(approvedAmount),
        termMonths,
        normalizeRate(annualInterestRate),
        normalize(estimatedInstallment),
        status,
        riskSnapshotJson,
        createdAt);
  }

  private static BigDecimal normalize(BigDecimal amount) {
    return amount.setScale(4, RoundingMode.HALF_UP);
  }

  private static BigDecimal normalizeRate(BigDecimal amount) {
    return amount.setScale(4, RoundingMode.HALF_UP);
  }
}
