package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.CreateLoanApplicationCommand;
import com.yeribank.core.application.dto.ListLoanApplicationsQuery;
import com.yeribank.core.application.dto.LoanApplicationResult;
import com.yeribank.core.application.dto.LoanOfferResult;
import com.yeribank.core.application.dto.LoanSimulationCommand;
import com.yeribank.core.application.port.in.LoanUseCase;
import com.yeribank.core.infrastructure.web.dto.CreateLoanApplicationRequest;
import com.yeribank.core.infrastructure.web.dto.LoanApplicationResponse;
import com.yeribank.core.infrastructure.web.dto.LoanSimulationRequest;
import com.yeribank.core.infrastructure.web.dto.LoanSimulationResponse;
import com.yeribank.core.infrastructure.web.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
@Tag(name = "Prestamos", description = "Simulacion y solicitudes de prestamos.")
public class LoanController {

  private final LoanUseCase loanUseCase;
  private final SecurityContextFacade securityContextFacade;

  public LoanController(LoanUseCase loanUseCase, SecurityContextFacade securityContextFacade) {
    this.loanUseCase = loanUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @PostMapping("/simulations")
  @Operation(summary = "Simular prestamo", description = "Calcula una oferta estimada segun perfil financiero y riesgo.")
  public ResponseEntity<LoanSimulationResponse> simulate(
      @Valid @RequestBody LoanSimulationRequest request) {
    LoanOfferResult result =
        loanUseCase.simulate(
            new LoanSimulationCommand(
                request.userId(),
                request.requestedAmount(),
                request.termMonths(),
                securityContextFacade.currentUserId(),
                securityContextFacade.isAdmin()));
    return ResponseEntity.ok(
        new LoanSimulationResponse(
            result.requestedAmount(),
            result.approvedAmount(),
            result.termMonths(),
            result.annualInterestRate(),
            result.estimatedInstallment(),
            result.status(),
            result.score(),
            result.reasons()));
  }

  @PostMapping("/applications")
  @Operation(summary = "Crear solicitud de prestamo", description = "Registra una solicitud de prestamo con decision y snapshot de riesgo.")
  public ResponseEntity<LoanApplicationResponse> createApplication(
      @Valid @RequestBody CreateLoanApplicationRequest request) {
    LoanApplicationResult result =
        loanUseCase.createApplication(
            new CreateLoanApplicationCommand(
                request.userId(),
                request.requestedAmount(),
                request.termMonths(),
                securityContextFacade.currentUserId(),
                securityContextFacade.isAdmin()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
  }

  @GetMapping("/applications")
  @Operation(summary = "Listar solicitudes", description = "Lista solicitudes propias o de un usuario concreto cuando el caller es admin.")
  public ResponseEntity<PageResponse<LoanApplicationResponse>> listApplications(
      @RequestParam(required = false) UUID userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        PageResponse.of(
            loanUseCase
                .listApplications(
                    new ListLoanApplicationsQuery(
                        userId, securityContextFacade.currentUserId(), securityContextFacade.isAdmin()))
                .stream()
                .map(this::toResponse)
                .toList(),
            page,
            size));
  }

  private LoanApplicationResponse toResponse(LoanApplicationResult result) {
    return new LoanApplicationResponse(
        result.id(),
        result.userId(),
        result.requestedAmount(),
        result.approvedAmount(),
        result.termMonths(),
        result.annualInterestRate(),
        result.estimatedInstallment(),
        result.status(),
        result.score(),
        result.reasons(),
        result.createdAt());
  }
}
