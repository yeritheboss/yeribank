package com.yeribank.core.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialProfileResult(
    UUID userId,
    BigDecimal monthlyIncome,
    BigDecimal monthlyExpenses,
    BigDecimal currentDebt,
    LocalDateTime updatedAt) {}
