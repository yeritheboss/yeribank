package com.yeribank.core.application.service;

import com.yeribank.core.application.dto.AccountResult;
import com.yeribank.core.application.dto.AccountTransferResult;
import com.yeribank.core.application.dto.DashboardResult;
import com.yeribank.core.application.dto.UserResult;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.DashboardUseCase;
import com.yeribank.core.application.port.out.AccountRepositoryPort;
import com.yeribank.core.application.port.out.TransferRepositoryPort;
import com.yeribank.core.application.port.out.UserRepositoryPort;
import com.yeribank.core.domain.model.Account;
import com.yeribank.core.domain.model.Transfer;
import com.yeribank.core.domain.model.User;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService implements DashboardUseCase {

  private static final int RECENT_TRANSFERS_LIMIT = 10;

  private final UserRepositoryPort userRepository;
  private final AccountRepositoryPort accountRepository;
  private final TransferRepositoryPort transferRepository;

  public DashboardService(
      UserRepositoryPort userRepository,
      AccountRepositoryPort accountRepository,
      TransferRepositoryPort transferRepository) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.transferRepository = transferRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public DashboardResult getMyDashboard(UUID actorUserId) {
    User user =
        userRepository
            .findById(actorUserId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
    List<Account> accounts = accountRepository.findByUserId(actorUserId);
    List<UUID> accountIds = accounts.stream().map(Account::id).toList();

    return new DashboardResult(
        toUserResult(user),
        accounts.stream().map(account -> toAccountResult(account, user)).toList(),
        transferRepository.findByAccountIds(accountIds, RECENT_TRANSFERS_LIMIT).stream()
            .map(transfer -> toTransferResult(transfer, accountIds))
            .toList());
  }

  private UserResult toUserResult(User user) {
    return new UserResult(
        user.id(),
        user.email(),
        user.role(),
        user.fullName(),
        user.age(),
        user.jobTitle(),
        user.createdAt());
  }

  private AccountResult toAccountResult(Account account, User owner) {
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

  private AccountTransferResult toTransferResult(Transfer transfer, List<UUID> accountIds) {
    Account fromAccount = loadAccount(transfer.fromAccountId(), "Source account not found");
    Account toAccount = loadAccount(transfer.toAccountId(), "Destination account not found");
    String direction = accountIds.contains(transfer.fromAccountId()) ? "OUTGOING" : "INCOMING";

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

  private Account loadAccount(UUID accountId, String message) {
    return accountRepository
        .findById(accountId)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, message));
  }
}
