package com.yeribank.core.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialProfileResponse(
    UUID userId,
    BigDecimal monthlyIncome,
    BigDecimal monthlyExpenses,
    BigDecimal currentDebt,
    LocalDateTime updatedAt) {}
