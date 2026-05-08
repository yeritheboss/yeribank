package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.AuditLog;
import java.util.List;

public interface AuditLogRepositoryPort {
  AuditLog save(AuditLog auditLog);

  List<AuditLog> findRecent(int limit);
}
