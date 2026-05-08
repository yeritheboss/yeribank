package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.TransferStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
    UUID id,
    UUID fromAccountId,
    UUID toAccountId,
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    TransferStatus status) {}
