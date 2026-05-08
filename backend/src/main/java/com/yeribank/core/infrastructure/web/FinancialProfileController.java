package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.FinancialProfileCommand;
import com.yeribank.core.application.dto.FinancialProfileResult;
import com.yeribank.core.application.dto.GetFinancialProfileQuery;
import com.yeribank.core.application.port.in.FinancialProfileUseCase;
import com.yeribank.core.infrastructure.web.dto.FinancialProfileRequest;
import com.yeribank.core.infrastructure.web.dto.FinancialProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/financial-profile")
@Tag(name = "Perfil financiero", description = "Gestion del perfil financiero usado por riesgo y prestamos.")
public class FinancialProfileController {

  private final FinancialProfileUseCase financialProfileUseCase;
  private final SecurityContextFacade securityContextFacade;

  public FinancialProfileController(
      FinancialProfileUseCase financialProfileUseCase, SecurityContextFacade securityContextFacade) {
    this.financialProfileUseCase = financialProfileUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @PostMapping
  @Operation(summary = "Crear o actualizar perfil financiero", description = "Guarda ingresos, gastos y deuda actual de un usuario.")
  public ResponseEntity<FinancialProfileResponse> upsert(
      @Valid @RequestBody FinancialProfileRequest request) {
    FinancialProfileResult result =
        financialProfileUseCase.upsert(
            new FinancialProfileCommand(
                request.userId(),
                request.monthlyIncome(),
                request.monthlyExpenses(),
                request.currentDebt(),
                securityContextFacade.currentUserId(),
                securityContextFacade.isAdmin()));
    return ResponseEntity.ok(toResponse(result));
  }

  @GetMapping("/me")
  @Operation(summary = "Consultar mi perfil financiero", description = "Devuelve el perfil financiero del usuario autenticado.")
  public ResponseEntity<FinancialProfileResponse> getMine() {
    return ResponseEntity.ok(toResponse(loadProfile(securityContextFacade.currentUserId())));
  }

  @GetMapping("/{userId}")
  @Operation(summary = "Consultar perfil financiero", description = "Devuelve el perfil financiero de un usuario permitido.")
  public ResponseEntity<FinancialProfileResponse> getByUser(@PathVariable UUID userId) {
    return ResponseEntity.ok(toResponse(loadProfile(userId)));
  }

  private FinancialProfileResult loadProfile(UUID userId) {
    return financialProfileUseCase.get(
        new GetFinancialProfileQuery(
            userId, securityContextFacade.currentUserId(), securityContextFacade.isAdmin()));
  }

  private FinancialProfileResponse toResponse(FinancialProfileResult result) {
    return new FinancialProfileResponse(
        result.userId(),
        result.monthlyIncome(),
        result.monthlyExpenses(),
        result.currentDebt(),
        result.updatedAt());
  }
}
