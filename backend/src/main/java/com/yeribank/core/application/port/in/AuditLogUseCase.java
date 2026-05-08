package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.AuditLogResult;
import com.yeribank.core.application.dto.ListAuditLogsQuery;
import java.util.List;

public interface AuditLogUseCase {
  List<AuditLogResult> listRecent(ListAuditLogsQuery query);
}
