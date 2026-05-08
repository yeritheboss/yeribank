package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.FraudAlertRepositoryPort;
import com.yeribank.core.domain.model.FraudAlert;
import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import com.yeribank.core.infrastructure.persistence.entity.FraudAlertJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.FraudAlertJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FraudAlertRepositoryAdapter implements FraudAlertRepositoryPort {

  private final FraudAlertJpaRepository repository;

  public FraudAlertRepositoryAdapter(FraudAlertJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public FraudAlert save(FraudAlert alert) {
    return toDomain(repository.save(toEntity(alert)));
  }

  @Override
  public Optional<FraudAlert> findById(UUID id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public List<FraudAlert> findAll() {
    return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
  }

  @Override
  public List<FraudAlert> findByStatus(FraudAlertStatus status) {
    return repository.findByStatusOrderByCreatedAtDesc(status).stream().map(this::toDomain).toList();
  }

  @Override
  public List<FraudAlert> findByUserId(UUID userId) {
    return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
  }

  @Override
  public List<FraudAlert> findByUserIdAndStatus(UUID userId, FraudAlertStatus status) {
    return repository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public long countByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime threshold) {
    return repository.countByUserIdAndCreatedAtAfter(userId, threshold);
  }

  private FraudAlert toDomain(FraudAlertJpaEntity entity) {
    return new FraudAlert(
        entity.getId(),
        entity.getTransferId(),
        entity.getUserId(),
        entity.getAccountId(),
        entity.getRuleCode(),
        entity.getSeverity(),
        entity.getStatus(),
        entity.getDetailsJson(),
        entity.getCreatedAt(),
        entity.getReviewedAt(),
        entity.getReviewedBy());
  }

  private FraudAlertJpaEntity toEntity(FraudAlert alert) {
    FraudAlertJpaEntity entity = new FraudAlertJpaEntity();
    entity.setId(alert.id());
    entity.setTransferId(alert.transferId());
    entity.setUserId(alert.userId());
    entity.setAccountId(alert.accountId());
    entity.setRuleCode(alert.ruleCode());
    entity.setSeverity(alert.severity());
    entity.setStatus(alert.status());
    entity.setDetailsJson(alert.detailsJson());
    entity.setCreatedAt(alert.createdAt());
    entity.setReviewedAt(alert.reviewedAt());
    entity.setReviewedBy(alert.reviewedBy());
    return entity;
  }
}
