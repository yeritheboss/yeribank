package com.yeribank.core.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record FinancialProfileRequest(
    UUID userId,
    @NotNull @DecimalMin(value = "0.0") BigDecimal monthlyIncome,
    @NotNull @DecimalMin(value = "0.0") BigDecimal monthlyExpenses,
    @NotNull @DecimalMin(value = "0.0") BigDecimal currentDebt) {}
