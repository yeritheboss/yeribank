package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.TransferRepositoryPort;
import com.yeribank.core.domain.model.Transfer;
import com.yeribank.core.infrastructure.persistence.entity.TransferJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.TransferJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class TransferRepositoryAdapter implements TransferRepositoryPort {

  private final TransferJpaRepository repository;

  public TransferRepositoryAdapter(TransferJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Transfer save(Transfer transfer) {
    return toDomain(repository.save(toEntity(transfer)));
  }

  @Override
  public Optional<Transfer> findById(UUID id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public List<Transfer> findByAccountId(UUID accountId, int limit) {
    return repository.findRecentByAccountId(accountId, PageRequest.of(0, Math.max(1, limit))).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public List<Transfer> findByAccountIds(List<UUID> accountIds, int limit) {
    if (accountIds.isEmpty()) {
      return List.of();
    }
    return repository.findRecentByAccountIds(accountIds, PageRequest.of(0, Math.max(1, limit))).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public void updateRiskScore(UUID transferId, int riskScore) {
    repository.findById(transferId).ifPresent(entity -> {
      entity.setRiskScore(riskScore);
      repository.save(entity);
    });
  }

  @Override
  public long countByFromAccountIdAndCreatedAtAfter(UUID fromAccountId, LocalDateTime threshold) {
    return repository.countByFromAccountIdAndCreatedAtAfter(fromAccountId, threshold);
  }

  @Override
  public long countByFromAccountIdAndToAccountId(UUID fromAccountId, UUID toAccountId) {
    return repository.countByFromAccountIdAndToAccountId(fromAccountId, toAccountId);
  }

  @Override
  public BigDecimal averageAmountByFromAccountIdAndCreatedAtAfter(
      UUID fromAccountId, LocalDateTime threshold) {
    var transfers = repository.findByFromAccountIdAndCreatedAtAfter(fromAccountId, threshold);
    return transfers.stream()
        .map(TransferJpaEntity::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(
            BigDecimal.valueOf(Math.max(1, transfers.size())),
            4,
            java.math.RoundingMode.HALF_UP);
  }

  private Transfer toDomain(TransferJpaEntity entity) {
    return new Transfer(
        entity.getId(),
        entity.getFromAccountId(),
        entity.getToAccountId(),
        entity.getAmount(),
        entity.getStatus(),
        entity.getRiskScore(),
        entity.getCreatedAt());
  }

  private TransferJpaEntity toEntity(Transfer transfer) {
    TransferJpaEntity entity = new TransferJpaEntity();
    entity.setId(transfer.id());
    entity.setFromAccountId(transfer.fromAccountId());
    entity.setToAccountId(transfer.toAccountId());
    entity.setAmount(transfer.amount());
    entity.setStatus(transfer.status());
    entity.setRiskScore(transfer.riskScore());
    entity.setCreatedAt(transfer.createdAt());
    return entity;
  }
}
