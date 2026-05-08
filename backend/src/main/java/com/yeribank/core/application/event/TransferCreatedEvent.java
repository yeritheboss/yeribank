package com.yeribank.core.application.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferCreatedEvent(
    UUID transferId,
    UUID fromAccountId,
    UUID toAccountId,
    BigDecimal amount,
    String status,
    LocalDateTime occurredAt) {}
