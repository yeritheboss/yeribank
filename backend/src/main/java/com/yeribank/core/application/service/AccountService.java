package com.yeribank.core.application.service;

import com.yeribank.core.application.dto.AccountResult;
import com.yeribank.core.application.dto.BalanceResult;
import com.yeribank.core.application.dto.CreateAccountCommand;
import com.yeribank.core.application.dto.GetBalanceQuery;
import com.yeribank.core.application.dto.ListAccountsQuery;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.AccountUseCase;
import com.yeribank.core.application.port.out.AccountRepositoryPort;
import com.yeribank.core.application.port.out.UserRepositoryPort;
import com.yeribank.core.domain.model.Account;
import com.yeribank.core.domain.model.User;
import com.yeribank.core.domain.model.enums.AccountStatus;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements AccountUseCase {

  private static final SecureRandom RANDOM = new SecureRandom();

  private final AccountRepositoryPort accountRepository;
  private final UserRepositoryPort userRepository;
  private final AuditLogService auditLogService;

  public AccountService(
      AccountRepositoryPort accountRepository,
      UserRepositoryPort userRepository,
      AuditLogService auditLogService) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.auditLogService = auditLogService;
  }

  @Override
  @Transactional
  public AccountResult create(CreateAccountCommand command) {
    UUID ownerUserId = resolveOwner(command);
    User owner =
        userRepository
        .findById(ownerUserId)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

    BigDecimal initialBalance =
        command.initialBalance() == null ? BigDecimal.ZERO : command.initialBalance();
    if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Initial balance cannot be negative");
    }

    String accountNumber = generateAccountNumber();
    Account saved =
        accountRepository.save(
            Account.create(UUID.randomUUID(), ownerUserId, accountNumber, initialBalance, AccountStatus.ACTIVE));

    auditLogService.record(
        command.actorUserId(),
        "ACCOUNT_CREATED",
        "ACCOUNT",
        saved.id().toString(),
        "SUCCESS",
        "{\"accountNumber\":\""
            + saved.accountNumber()
            + "\",\"ownerUserId\":\""
            + saved.userId()
            + "\"}");
    return toResult(saved, owner);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AccountResult> list(ListAccountsQuery query) {
    UUID targetUserId = query.actorAdmin() && query.userId() != null ? query.userId() : query.actorUserId();
    if (query.actorAdmin() && query.userId() == null) {
      return accountRepository.findAll().stream().map(this::toResult).toList();
    }
    return accountRepository.findByUserId(targetUserId).stream().map(this::toResult).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public BalanceResult getBalance(GetBalanceQuery query) {
    Account account =
        accountRepository
            .findById(query.accountId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Account not found"));

    assertCanAccess(account, query.actorUserId(), query.actorAdmin());
    return new BalanceResult(account.id(), account.balance());
  }

  private UUID resolveOwner(CreateAccountCommand command) {
    if (command.actorAdmin() && command.ownerUserId() != null) {
      return command.ownerUserId();
    }
    return command.actorUserId();
  }

  private void assertCanAccess(Account account, UUID actorUserId, boolean actorAdmin) {
    if (!actorAdmin && !account.belongsTo(actorUserId)) {
      throw new AppException(HttpStatus.FORBIDDEN, "You cannot access this account");
    }
  }

  private String generateAccountNumber() {
    String accountNumber;
    do {
      accountNumber = "ES20" + randomDigits(20);
    } while (accountRepository.existsByAccountNumber(accountNumber));
    return accountNumber;
  }

  private AccountResult toResult(Account account) {
    User owner =
        userRepository
            .findById(account.userId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Account owner not found"));
    return toResult(account, owner);
  }

  private AccountResult toResult(Account account, User owner) {
    return new AccountResult(
        account.id(),
        account.userId(),
        account.accountNumber(),
        account.balance(),
        account.status(),
        owner.email(),
        owner.fullName(),
        owner.age(),
        owner.jobTitle());
  }

  private String randomDigits(int length) {
    StringBuilder value = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      value.append(RANDOM.nextInt(10));
    }
    return value.toString();
  }
}
