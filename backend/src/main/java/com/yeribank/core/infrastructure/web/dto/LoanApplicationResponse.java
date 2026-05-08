package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.LoanApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LoanApplicationResponse(
    UUID id,
    UUID userId,
    BigDecimal requestedAmount,
    BigDecimal approvedAmount,
    int termMonths,
    BigDecimal annualInterestRate,
    BigDecimal estimatedInstallment,
    LoanApplicationStatus status,
    int score,
    List<String> reasons,
    LocalDateTime createdAt) {}
