package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.AccountResult;
import com.yeribank.core.application.dto.AccountTransferResult;
import com.yeribank.core.application.dto.DashboardResult;
import com.yeribank.core.application.dto.UserResult;
import com.yeribank.core.application.port.in.DashboardUseCase;
import com.yeribank.core.infrastructure.web.dto.AccountResponse;
import com.yeribank.core.infrastructure.web.dto.AccountTransferResponse;
import com.yeribank.core.infrastructure.web.dto.DashboardResponse;
import com.yeribank.core.infrastructure.web.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Resumen del usuario autenticado para interfaces de cliente.")
public class DashboardController {

  private final DashboardUseCase dashboardUseCase;
  private final SecurityContextFacade securityContextFacade;

  public DashboardController(
      DashboardUseCase dashboardUseCase, SecurityContextFacade securityContextFacade) {
    this.dashboardUseCase = dashboardUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @GetMapping("/me")
  @Operation(summary = "Consultar mi dashboard", description = "Devuelve usuario, cuentas y ultimos movimientos.")
  public ResponseEntity<DashboardResponse> getMyDashboard() {
    return ResponseEntity.ok(toResponse(dashboardUseCase.getMyDashboard(securityContextFacade.currentUserId())));
  }

  private DashboardResponse toResponse(DashboardResult result) {
    return new DashboardResponse(
        toUserResponse(result.user()),
        result.accounts().stream().map(this::toAccountResponse).toList(),
        result.recentTransfers().stream().map(this::toTransferResponse).toList());
  }

  private UserResponse toUserResponse(UserResult user) {
    return new UserResponse(
        user.id(),
        user.email(),
        user.role(),
        user.fullName(),
        user.age(),
        user.jobTitle(),
        user.createdAt());
  }

  private AccountResponse toAccountResponse(AccountResult account) {
    return new AccountResponse(
        account.id(),
        account.userId(),
        account.accountNumber(),
        account.balance(),
        account.status(),
        account.ownerEmail(),
        account.ownerFullName(),
        account.ownerAge(),
        account.ownerJobTitle());
  }

  private AccountTransferResponse toTransferResponse(AccountTransferResult transfer) {
    return new AccountTransferResponse(
        transfer.id(),
        transfer.fromAccountId(),
        transfer.toAccountId(),
        transfer.fromAccountNumber(),
        transfer.toAccountNumber(),
        transfer.amount(),
        transfer.status(),
        transfer.riskScore(),
        transfer.createdAt(),
        transfer.direction());
  }
}
