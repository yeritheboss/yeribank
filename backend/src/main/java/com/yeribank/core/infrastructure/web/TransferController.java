package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.ExecuteTransferCommand;
import com.yeribank.core.application.dto.TransferResult;
import com.yeribank.core.application.port.in.TransferUseCase;
import com.yeribank.core.infrastructure.web.dto.TransferRequest;
import com.yeribank.core.infrastructure.web.dto.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
@Tag(name = "Transferencias", description = "Ejecucion de transferencias entre cuentas.")
public class TransferController {

  private final TransferUseCase transferUseCase;
  private final SecurityContextFacade securityContextFacade;

  public TransferController(
      TransferUseCase transferUseCase, SecurityContextFacade securityContextFacade) {
    this.transferUseCase = transferUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @PostMapping
  @Operation(summary = "Ejecutar transferencia", description = "Transfiere dinero entre cuentas y dispara el analisis de riesgo.")
  public ResponseEntity<TransferResponse> execute(@Valid @RequestBody TransferRequest request) {
    TransferResult result =
        transferUseCase.execute(
            new ExecuteTransferCommand(
                request.fromAccountId(),
                request.toAccountId(),
                request.fromAccountNumber(),
                request.toAccountNumber(),
                request.amount(),
                securityContextFacade.currentUserId(),
                securityContextFacade.isAdmin()));

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new TransferResponse(
                result.id(),
                result.fromAccountId(),
                result.toAccountId(),
                result.fromAccountNumber(),
                result.toAccountNumber(),
                result.amount(),
                result.status()));
  }
}
