package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.AuditLogRepositoryPort;
import com.yeribank.core.domain.model.AuditLog;
import com.yeribank.core.infrastructure.persistence.entity.AuditLogJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.AuditLogJpaRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {

  private final AuditLogJpaRepository repository;

  public AuditLogRepositoryAdapter(AuditLogJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public AuditLog save(AuditLog auditLog) {
    return toDomain(repository.save(toEntity(auditLog)));
  }

  @Override
  public List<AuditLog> findRecent(int limit) {
    return repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, Math.max(1, limit))).stream()
        .map(this::toDomain)
        .toList();
  }

  private AuditLog toDomain(AuditLogJpaEntity entity) {
    return new AuditLog(
        entity.getId(),
        entity.getActorUserId(),
        entity.getAction(),
        entity.getResourceType(),
        entity.getResourceId(),
        entity.getStatus(),
        entity.getDetailsJson(),
        entity.getCreatedAt());
  }

  private AuditLogJpaEntity toEntity(AuditLog auditLog) {
    AuditLogJpaEntity entity = new AuditLogJpaEntity();
    entity.setId(auditLog.id());
    entity.setActorUserId(auditLog.actorUserId());
    entity.setAction(auditLog.action());
    entity.setResourceType(auditLog.resourceType());
    entity.setResourceId(auditLog.resourceId());
    entity.setStatus(auditLog.status());
    entity.setDetailsJson(auditLog.detailsJson());
    entity.setCreatedAt(auditLog.createdAt());
    return entity;
  }
}
