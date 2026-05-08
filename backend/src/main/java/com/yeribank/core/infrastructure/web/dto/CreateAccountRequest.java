package com.yeribank.core.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountRequest(
    UUID userId,
    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance cannot be negative")
        BigDecimal initialBalance) {}
