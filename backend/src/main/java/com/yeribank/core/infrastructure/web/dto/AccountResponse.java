package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.AccountStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
    UUID id,
    UUID userId,
    String accountNumber,
    BigDecimal balance,
    AccountStatus status,
    String ownerEmail,
    String ownerFullName,
    Integer ownerAge,
    String ownerJobTitle) {}
