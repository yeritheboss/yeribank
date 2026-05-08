package com.yeribank.core.application.service;

import com.yeribank.core.application.dto.AuditLogResult;
import com.yeribank.core.application.dto.ListAuditLogsQuery;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.AuditLogUseCase;
import com.yeribank.core.application.port.out.AuditLogRepositoryPort;
import com.yeribank.core.domain.model.AuditLog;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService implements AuditLogUseCase {

  private final AuditLogRepositoryPort auditLogRepository;

  public AuditLogService(AuditLogRepositoryPort auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  @Transactional
  public void record(
      UUID actorUserId,
      String action,
      String resourceType,
      String resourceId,
      String status,
      String detailsJson) {
    auditLogRepository.save(
        AuditLog.create(actorUserId, action, resourceType, resourceId, status, detailsJson));
  }

  @Override
  @Transactional(readOnly = true)
  public List<AuditLogResult> listRecent(ListAuditLogsQuery query) {
    if (!query.actorAdmin()) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only ADMIN can view audit logs");
    }

    int limit = Math.max(1, Math.min(query.limit(), 200));
    return auditLogRepository.findRecent(limit).stream().map(this::toResult).toList();
  }

  private AuditLogResult toResult(AuditLog auditLog) {
    return new AuditLogResult(
        auditLog.id(),
        auditLog.actorUserId(),
        auditLog.action(),
        auditLog.resourceType(),
        auditLog.resourceId(),
        auditLog.status(),
        auditLog.detailsJson(),
        auditLog.createdAt());
  }
}
