package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.ListFraudAlertsQuery;
import com.yeribank.core.application.dto.ReviewFraudAlertCommand;
import com.yeribank.core.application.port.in.FraudAlertUseCase;
import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import com.yeribank.core.infrastructure.web.dto.FraudAlertResponse;
import com.yeribank.core.infrastructure.web.dto.PageResponse;
import com.yeribank.core.infrastructure.web.dto.ReviewFraudAlertRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fraud/alerts")
@Tag(name = "Fraude", description = "Consulta y revision de alertas de fraude.")
public class FraudController {

  private final FraudAlertUseCase fraudAlertUseCase;
  private final SecurityContextFacade securityContextFacade;

  public FraudController(
      FraudAlertUseCase fraudAlertUseCase, SecurityContextFacade securityContextFacade) {
    this.fraudAlertUseCase = fraudAlertUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @GetMapping
  @Operation(summary = "Listar alertas", description = "Lista alertas de fraude, opcionalmente filtradas por estado.")
  public ResponseEntity<PageResponse<FraudAlertResponse>> listAlerts(
      @RequestParam(required = false) FraudAlertStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        PageResponse.of(
            fraudAlertUseCase
                .listAlerts(
                    new ListFraudAlertsQuery(
                        status, securityContextFacade.currentUserId(), securityContextFacade.isAdmin()))
                .stream()
                .map(this::toResponse)
                .toList(),
            page,
            size));
  }

  @PatchMapping("/{alertId}/status")
  @Operation(summary = "Revisar alerta", description = "Actualiza el estado de una alerta de fraude.")
  public ResponseEntity<FraudAlertResponse> reviewAlert(
      @PathVariable UUID alertId, @Valid @RequestBody ReviewFraudAlertRequest request) {
    var result =
        fraudAlertUseCase.reviewAlert(
            new ReviewFraudAlertCommand(
                alertId,
                request.status(),
                securityContextFacade.currentUserId(),
                securityContextFacade.isAdmin()));

    return ResponseEntity.ok(
        toResponse(result));
  }

  private FraudAlertResponse toResponse(com.yeribank.core.application.dto.FraudAlertResult result) {
    return new FraudAlertResponse(
        result.id(),
        result.transferId(),
        result.userId(),
        result.accountId(),
        result.ruleCode(),
        result.severity(),
        result.status(),
        result.details(),
        result.createdAt(),
        result.reviewedAt(),
        result.reviewedBy());
  }
}
