package com.yeribank.core.application.dto;

import com.yeribank.core.domain.model.enums.FraudAlertSeverity;
import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record FraudAlertResult(
    UUID id,
    UUID transferId,
    UUID userId,
    UUID accountId,
    String ruleCode,
    FraudAlertSeverity severity,
    FraudAlertStatus status,
    Map<String, Object> details,
    LocalDateTime createdAt,
    LocalDateTime reviewedAt,
    UUID reviewedBy) {}
