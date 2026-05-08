package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.AccountRepositoryPort;
import com.yeribank.core.domain.model.Account;
import com.yeribank.core.infrastructure.persistence.entity.AccountJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.AccountJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AccountRepositoryAdapter implements AccountRepositoryPort {

  private final AccountJpaRepository repository;

  public AccountRepositoryAdapter(AccountJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Account save(Account account) {
    return toDomain(repository.save(toEntity(account)));
  }

  @Override
  public Optional<Account> findById(UUID id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<Account> findByAccountNumber(String accountNumber) {
    return repository.findByAccountNumber(accountNumber).map(this::toDomain);
  }

  @Override
  public List<Account> findByUserId(UUID userId) {
    return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
  }

  @Override
  public List<Account> findAll() {
    return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
  }

  @Override
  public boolean existsByAccountNumber(String accountNumber) {
    return repository.existsByAccountNumber(accountNumber);
  }

  private Account toDomain(AccountJpaEntity entity) {
    return new Account(
        entity.getId(),
        entity.getUserId(),
        entity.getAccountNumber(),
        entity.getBalance(),
        entity.getStatus(),
        entity.getVersion(),
        entity.getCreatedAt());
  }

  private AccountJpaEntity toEntity(Account account) {
    AccountJpaEntity entity = new AccountJpaEntity();
    entity.setId(account.id());
    entity.setUserId(account.userId());
    entity.setAccountNumber(account.accountNumber());
    entity.setBalance(account.balance());
    entity.setStatus(account.status());
    entity.setVersion(account.version());
    entity.setCreatedAt(account.createdAt());
    return entity;
  }
}
