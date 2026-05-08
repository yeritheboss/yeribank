package com.yeribank.core.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResult(
    UUID id,
    UUID actorUserId,
    String action,
    String resourceType,
    String resourceId,
    String status,
    String detailsJson,
    LocalDateTime createdAt) {}
