package com.yeribank.core.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record LoanSimulationRequest(
    UUID userId,
    @NotNull @DecimalMin(value = "0.01") BigDecimal requestedAmount,
    @Min(6) @Max(72) int termMonths) {}
