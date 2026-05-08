package com.yeribank.core.domain.model;

import com.yeribank.core.domain.model.enums.AccountStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Account(
    UUID id,
    UUID userId,
    String accountNumber,
    BigDecimal balance,
    AccountStatus status,
    long version,
    LocalDateTime createdAt) {

  public static Account create(
      UUID id, UUID userId, String accountNumber, BigDecimal initialBalance, AccountStatus status) {
    return new Account(id, userId, accountNumber, normalize(initialBalance), status, 0L, LocalDateTime.now());
  }

  public boolean belongsTo(UUID candidateUserId) {
    return userId.equals(candidateUserId);
  }

  public boolean canOperate() {
    return status == AccountStatus.ACTIVE;
  }

  public boolean hasEnoughBalance(BigDecimal amount) {
    return balance.compareTo(normalize(amount)) >= 0;
  }

  public Account debit(BigDecimal amount) {
    BigDecimal normalizedAmount = normalize(amount);
    return new Account(id, userId, accountNumber, balance.subtract(normalizedAmount), status, version, createdAt);
  }

  public Account credit(BigDecimal amount) {
    BigDecimal normalizedAmount = normalize(amount);
    return new Account(id, userId, accountNumber, balance.add(normalizedAmount), status, version, createdAt);
  }

  private static BigDecimal normalize(BigDecimal amount) {
    return amount.setScale(4, java.math.RoundingMode.HALF_UP);
  }
}
