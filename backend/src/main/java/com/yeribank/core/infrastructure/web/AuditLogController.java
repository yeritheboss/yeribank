package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.AuditLogResult;
import com.yeribank.core.application.dto.ListAuditLogsQuery;
import com.yeribank.core.application.port.in.AuditLogUseCase;
import com.yeribank.core.infrastructure.web.dto.AuditLogResponse;
import com.yeribank.core.infrastructure.web.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit-logs")
@Tag(name = "Auditoria", description = "Consulta de eventos auditables del sistema.")
public class AuditLogController {

  private final AuditLogUseCase auditLogUseCase;
  private final SecurityContextFacade securityContextFacade;

  public AuditLogController(
      AuditLogUseCase auditLogUseCase, SecurityContextFacade securityContextFacade) {
    this.auditLogUseCase = auditLogUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @GetMapping
  @Operation(summary = "Listar auditoria", description = "Devuelve eventos recientes de auditoria. Solo ADMIN.")
  public ResponseEntity<PageResponse<AuditLogResponse>> listRecent(
      @RequestParam(required = false) String action,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) java.util.UUID actorUserId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        PageResponse.of(
            auditLogUseCase
                .listRecent(
                    new ListAuditLogsQuery(
                        securityContextFacade.currentUserId(),
                        securityContextFacade.isAdmin(),
                        Math.max(200, (page + 1) * Math.max(1, size))))
                .stream()
                .map(this::toResponse)
                .filter(response -> action == null || action.equalsIgnoreCase(response.action()))
                .filter(response -> status == null || status.equalsIgnoreCase(response.status()))
                .filter(response -> actorUserId == null || actorUserId.equals(response.actorUserId()))
                .toList(),
            page,
            size));
  }

  private AuditLogResponse toResponse(AuditLogResult result) {
    return new AuditLogResponse(
        result.id(),
        result.actorUserId(),
        result.action(),
        result.resourceType(),
        result.resourceId(),
        result.status(),
        result.detailsJson(),
        result.createdAt());
  }
}
