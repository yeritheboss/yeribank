package com.yeribank.core.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
    UUID fromAccountId,
    UUID toAccountId,
    String fromAccountNumber,
    String toAccountNumber,
    @NotNull(message = "amount is required")
        @DecimalMin(value = "0.0001", inclusive = true, message = "Amount must be positive")
        BigDecimal amount) {}
