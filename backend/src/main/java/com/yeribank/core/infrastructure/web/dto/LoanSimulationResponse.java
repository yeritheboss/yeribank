package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.LoanApplicationStatus;
import java.math.BigDecimal;
import java.util.List;

public record LoanSimulationResponse(
    BigDecimal requestedAmount,
    BigDecimal approvedAmount,
    int termMonths,
    BigDecimal annualInterestRate,
    BigDecimal estimatedInstallment,
    LoanApplicationStatus status,
    int score,
    List<String> reasons) {}
