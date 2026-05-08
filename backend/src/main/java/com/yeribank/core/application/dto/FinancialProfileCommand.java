package com.yeribank.core.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FinancialProfileCommand(
    UUID requestedUserId,
    BigDecimal monthlyIncome,
    BigDecimal monthlyExpenses,
    BigDecimal currentDebt,
    UUID actorUserId,
    boolean actorAdmin) {}
