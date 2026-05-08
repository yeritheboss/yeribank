package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.AuditLogJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
  List<AuditLogJpaEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
