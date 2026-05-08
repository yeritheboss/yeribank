package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.GetRiskProfileQuery;
import com.yeribank.core.application.dto.GetTransferRiskAssessmentQuery;
import com.yeribank.core.application.dto.RiskProfileResult;
import com.yeribank.core.application.dto.TransferRiskAssessmentResult;
import com.yeribank.core.application.port.in.RiskQueryUseCase;
import com.yeribank.core.infrastructure.web.dto.RiskProfileResponse;
import com.yeribank.core.infrastructure.web.dto.TransferRiskAssessmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/risk")
@Tag(name = "Riesgo", description = "Consulta de perfiles y evaluaciones de riesgo.")
public class RiskController {

  private final RiskQueryUseCase riskQueryUseCase;
  private final SecurityContextFacade securityContextFacade;

  public RiskController(
      RiskQueryUseCase riskQueryUseCase, SecurityContextFacade securityContextFacade) {
    this.riskQueryUseCase = riskQueryUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @GetMapping("/profile/me")
  @Operation(summary = "Consultar mi perfil de riesgo", description = "Devuelve el perfil de riesgo del usuario autenticado.")
  public ResponseEntity<RiskProfileResponse> getMyProfile() {
    return ResponseEntity.ok(toProfileResponse(loadProfile(securityContextFacade.currentUserId())));
  }

  @GetMapping("/profile/{userId}")
  @Operation(summary = "Consultar perfil de riesgo", description = "Devuelve el perfil de riesgo de un usuario permitido.")
  public ResponseEntity<RiskProfileResponse> getProfile(@PathVariable UUID userId) {
    return ResponseEntity.ok(toProfileResponse(loadProfile(userId)));
  }

  @GetMapping("/assessments/transfers/{transferId}")
  @Operation(summary = "Consultar riesgo de transferencia", description = "Obtiene la evaluacion de riesgo asociada a una transferencia.")
  public ResponseEntity<TransferRiskAssessmentResponse> getTransferAssessment(
      @PathVariable UUID transferId) {
    TransferRiskAssessmentResult result =
        riskQueryUseCase.getTransferAssessment(
            new GetTransferRiskAssessmentQuery(
                transferId, securityContextFacade.currentUserId(), securityContextFacade.isAdmin()));
    return ResponseEntity.ok(
        new TransferRiskAssessmentResponse(
            result.transferId(),
            result.userId(),
            result.score(),
            result.decision(),
            result.riskLevel(),
            result.reasons(),
            result.createdAt()));
  }

  private RiskProfileResult loadProfile(UUID userId) {
    return riskQueryUseCase.getProfile(
        new GetRiskProfileQuery(
            userId, securityContextFacade.currentUserId(), securityContextFacade.isAdmin()));
  }

  private RiskProfileResponse toProfileResponse(RiskProfileResult result) {
    return new RiskProfileResponse(
        result.userId(),
        result.score(),
        result.lastAssessmentScore(),
        result.riskLevel(),
        result.alertCount90d(),
        result.updatedAt());
  }
}
