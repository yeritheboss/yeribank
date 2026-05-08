package com.yeribank.core.application.service;

import com.yeribank.core.application.dto.AccountTransferResult;
import com.yeribank.core.application.dto.ExecuteTransferCommand;
import com.yeribank.core.application.dto.ListAccountTransfersQuery;
import com.yeribank.core.application.dto.TransferResult;
import com.yeribank.core.application.event.TransferCreatedEvent;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.TransferUseCase;
import com.yeribank.core.application.port.out.AccountRepositoryPort;
import com.yeribank.core.application.port.out.TransferRepositoryPort;
import com.yeribank.core.domain.model.Account;
import com.yeribank.core.domain.model.Transfer;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class TransferService implements TransferUseCase {

  private final AccountRepositoryPort accountRepository;
  private final TransferRepositoryPort transferRepository;
  private final TransferPostCommitHandler transferPostCommitHandler;
  private final AuditLogService auditLogService;

  public TransferService(
      AccountRepositoryPort accountRepository,
      TransferRepositoryPort transferRepository,
      TransferPostCommitHandler transferPostCommitHandler,
      AuditLogService auditLogService) {
    this.accountRepository = accountRepository;
    this.transferRepository = transferRepository;
    this.transferPostCommitHandler = transferPostCommitHandler;
    this.auditLogService = auditLogService;
  }

  @Override
  @Transactional
  public TransferResult execute(ExecuteTransferCommand command) {
    validateCommand(command);

    Account fromAccount = resolveAccount(command.fromAccountId(), command.fromAccountNumber(), "Source");
    Account toAccount = resolveAccount(command.toAccountId(), command.toAccountNumber(), "Destination");

    if (fromAccount.id().equals(toAccount.id())) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Source and destination accounts must differ");
    }

    assertCanOperate(fromAccount, command.actorUserId(), command.actorAdmin());
    if (!fromAccount.canOperate() || !toAccount.canOperate()) {
      throw new AppException(HttpStatus.CONFLICT, "Both accounts must be active");
    }
    if (!fromAccount.hasEnoughBalance(command.amount())) {
      throw new AppException(HttpStatus.CONFLICT, "Insufficient balance");
    }

    Account debited = accountRepository.save(fromAccount.debit(command.amount()));
    Account credited = accountRepository.save(toAccount.credit(command.amount()));

    Transfer saved =
        transferRepository.save(
            Transfer.create(UUID.randomUUID(), debited.id(), credited.id(), command.amount()));

    auditLogService.record(
        command.actorUserId(),
        "TRANSFER_EXECUTED",
        "TRANSFER",
        saved.id().toString(),
        "SUCCESS",
        "{\"fromAccountNumber\":\""
            + fromAccount.accountNumber()
            + "\",\"toAccountNumber\":\""
            + toAccount.accountNumber()
            + "\",\"amount\":\""
            + saved.amount()
            + "\"}");

    TransferCreatedEvent event =
        new TransferCreatedEvent(
            saved.id(),
            saved.fromAccountId(),
            saved.toAccountId(),
            saved.amount(),
            saved.status().name(),
            saved.createdAt());
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            transferPostCommitHandler.handle(event);
          }
        });

    return new TransferResult(
        saved.id(),
        saved.fromAccountId(),
        saved.toAccountId(),
        fromAccount.accountNumber(),
        toAccount.accountNumber(),
        saved.amount(),
        saved.status());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AccountTransferResult> listAccountTransfers(ListAccountTransfersQuery query) {
    Account account =
        accountRepository
            .findByAccountNumber(normalizeAccountNumber(query.accountNumber()))
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Account not found"));

    assertCanAccess(account, query.actorUserId(), query.actorAdmin());

    int limit = Math.max(1, Math.min(query.limit(), 100));
    return transferRepository.findByAccountId(account.id(), limit).stream()
        .map(transfer -> toAccountTransferResult(transfer, account.id()))
        .toList();
  }

  private void validateCommand(ExecuteTransferCommand command) {
    boolean missingSource = command.fromAccountId() == null && isBlank(command.fromAccountNumber());
    boolean missingDestination = command.toAccountId() == null && isBlank(command.toAccountNumber());
    if (missingSource) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Source account is required");
    }
    if (missingDestination) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Destination account is required");
    }
    if (command.amount() == null || command.amount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Transfer amount must be positive");
    }
  }

  private void assertCanOperate(Account account, UUID actorUserId, boolean actorAdmin) {
    assertCanAccess(account, actorUserId, actorAdmin);
  }

  private void assertCanAccess(Account account, UUID actorUserId, boolean actorAdmin) {
    if (!actorAdmin && !account.belongsTo(actorUserId)) {
      throw new AppException(HttpStatus.FORBIDDEN, "You cannot operate this source account");
    }
  }

  private Account resolveAccount(UUID accountId, String accountNumber, String label) {
    if (!isBlank(accountNumber)) {
      return accountRepository
          .findByAccountNumber(normalizeAccountNumber(accountNumber))
          .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, label + " account not found"));
    }

    return accountRepository
        .findById(accountId)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, label + " account not found"));
  }

  private String normalizeAccountNumber(String accountNumber) {
    return accountNumber.replace(" ", "").trim().toUpperCase();
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private AccountTransferResult toAccountTransferResult(Transfer transfer, UUID perspectiveAccountId) {
    Account fromAccount =
        accountRepository
            .findById(transfer.fromAccountId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Source account not found"));
    Account toAccount =
        accountRepository
            .findById(transfer.toAccountId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Destination account not found"));

    String direction = transfer.fromAccountId().equals(perspectiveAccountId) ? "OUTGOING" : "INCOMING";
    return new AccountTransferResult(
        transfer.id(),
        transfer.fromAccountId(),
        transfer.toAccountId(),
        fromAccount.accountNumber(),
        toAccount.accountNumber(),
        transfer.amount(),
        transfer.status(),
        transfer.riskScore(),
        transfer.createdAt(),
        direction);
  }
}
