package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.TransferStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountTransferResponse(
    UUID id,
    UUID fromAccountId,
    UUID toAccountId,
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    TransferStatus status,
    Integer riskScore,
    LocalDateTime createdAt,
    String direction) {}
