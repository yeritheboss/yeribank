package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
  Account save(Account account);

  Optional<Account> findById(UUID id);

  Optional<Account> findByAccountNumber(String accountNumber);

  List<Account> findByUserId(UUID userId);

  List<Account> findAll();

  boolean existsByAccountNumber(String accountNumber);
}
