package com.yeribank.core.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanSimulationCommand(
    UUID requestedUserId,
    BigDecimal requestedAmount,
    int termMonths,
    UUID actorUserId,
    boolean actorAdmin) {}
