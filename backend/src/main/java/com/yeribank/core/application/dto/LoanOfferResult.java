package com.yeribank.core.application.dto;

import com.yeribank.core.domain.model.enums.LoanApplicationStatus;
import java.math.BigDecimal;
import java.util.List;

public record LoanOfferResult(
    BigDecimal requestedAmount,
    BigDecimal approvedAmount,
    int termMonths,
    BigDecimal annualInterestRate,
    BigDecimal estimatedInstallment,
    LoanApplicationStatus status,
    int score,
    List<String> reasons) {}
