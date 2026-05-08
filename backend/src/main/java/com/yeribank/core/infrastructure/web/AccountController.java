package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.AccountResult;
import com.yeribank.core.application.dto.AccountTransferResult;
import com.yeribank.core.application.dto.BalanceResult;
import com.yeribank.core.application.dto.CreateAccountCommand;
import com.yeribank.core.application.dto.GetBalanceQuery;
import com.yeribank.core.application.dto.ListAccountTransfersQuery;
import com.yeribank.core.application.dto.ListAccountsQuery;
import com.yeribank.core.application.port.in.AccountUseCase;
import com.yeribank.core.application.port.in.TransferUseCase;
import com.yeribank.core.infrastructure.web.dto.AccountResponse;
import com.yeribank.core.infrastructure.web.dto.AccountTransferResponse;
import com.yeribank.core.infrastructure.web.dto.BalanceResponse;
import com.yeribank.core.infrastructure.web.dto.CreateAccountRequest;
import com.yeribank.core.infrastructure.web.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Cuentas", description = "Creacion de cuentas y consulta de saldos.")
public class AccountController {

  private final AccountUseCase accountUseCase;
  private final TransferUseCase transferUseCase;
  private final SecurityContextFacade securityContextFacade;

  public AccountController(
      AccountUseCase accountUseCase,
      TransferUseCase transferUseCase,
      SecurityContextFacade securityContextFacade) {
    this.accountUseCase = accountUseCase;
    this.transferUseCase = transferUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @PostMapping
  @Operation(summary = "Crear cuenta", description = "Crea una cuenta bancaria para el usuario autenticado o para otro usuario si es admin.")
  public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
    AccountResult result =
        accountUseCase.create(
            new CreateAccountCommand(
                request.userId(),
                request.initialBalance(),
                securityContextFacade.currentUserId(),
                securityContextFacade.isAdmin()));

    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
  }

  @GetMapping
  @Operation(summary = "Listar cuentas", description = "Lista cuentas propias. Un admin puede ver todas o filtrar por userId.")
  public ResponseEntity<PageResponse<AccountResponse>> list(
      @RequestParam(required = false) UUID userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        PageResponse.of(
            accountUseCase
                .list(
                    new ListAccountsQuery(
                        userId, securityContextFacade.currentUserId(), securityContextFacade.isAdmin()))
                .stream()
                .map(this::toResponse)
                .toList(),
            page,
            size));
  }

  @GetMapping("/{id}/balance")
  @Operation(summary = "Consultar saldo", description = "Obtiene el saldo de una cuenta si pertenece al usuario autenticado o si el usuario es admin.")
  public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID id) {
    BalanceResult result =
        accountUseCase.getBalance(
            new GetBalanceQuery(
                id, securityContextFacade.currentUserId(), securityContextFacade.isAdmin()));
    return ResponseEntity.ok(new BalanceResponse(result.accountId(), result.balance()));
  }

  @GetMapping("/{accountNumber}/transfers")
  @Operation(summary = "Listar movimientos de cuenta", description = "Lista transferencias entrantes y salientes de una cuenta por numero de cuenta.")
  public ResponseEntity<PageResponse<AccountTransferResponse>> listTransfers(
      @PathVariable String accountNumber,
      @RequestParam(required = false) String direction,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        PageResponse.of(
            transferUseCase
                .listAccountTransfers(
                    new ListAccountTransfersQuery(
                        accountNumber,
                        securityContextFacade.currentUserId(),
                        securityContextFacade.isAdmin(),
                        Math.max(100, (page + 1) * Math.max(1, size))))
                .stream()
                .map(this::toTransferResponse)
                .filter(response -> direction == null || direction.equalsIgnoreCase(response.direction()))
                .toList(),
            page,
            size));
  }

  private AccountResponse toResponse(AccountResult result) {
    return new AccountResponse(
        result.id(),
        result.userId(),
        result.accountNumber(),
        result.balance(),
        result.status(),
        result.ownerEmail(),
        result.ownerFullName(),
        result.ownerAge(),
        result.ownerJobTitle());
  }

  private AccountTransferResponse toTransferResponse(AccountTransferResult result) {
    return new AccountTransferResponse(
        result.id(),
        result.fromAccountId(),
        result.toAccountId(),
        result.fromAccountNumber(),
        result.toAccountNumber(),
        result.amount(),
        result.status(),
        result.riskScore(),
        result.createdAt(),
        result.direction());
  }
}
