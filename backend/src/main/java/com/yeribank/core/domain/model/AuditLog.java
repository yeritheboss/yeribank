package com.yeribank.core.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLog(
    UUID id,
    UUID actorUserId,
    String action,
    String resourceType,
    String resourceId,
    String status,
    String detailsJson,
    LocalDateTime createdAt) {

  public static AuditLog create(
      UUID actorUserId,
      String action,
      String resourceType,
      String resourceId,
      String status,
      String detailsJson) {
    return new AuditLog(
        UUID.randomUUID(),
        actorUserId,
        action,
        resourceType,
        resourceId,
        status,
        detailsJson,
        LocalDateTime.now());
  }
}
