package com.yeribank.core.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
    UUID id,
    UUID actorUserId,
    String action,
    String resourceType,
    String resourceId,
    String status,
    String detailsJson,
    LocalDateTime createdAt) {}
