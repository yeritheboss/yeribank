package com.yeribank.core.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ExecuteTransferCommand(
    UUID fromAccountId,
    UUID toAccountId,
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    UUID actorUserId,
    boolean actorAdmin) {}
