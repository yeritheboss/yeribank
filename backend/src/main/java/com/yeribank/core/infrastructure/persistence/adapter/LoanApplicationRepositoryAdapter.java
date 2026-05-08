package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.LoanApplicationRepositoryPort;
import com.yeribank.core.domain.model.LoanApplication;
import com.yeribank.core.infrastructure.persistence.entity.LoanApplicationJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.LoanApplicationJpaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepositoryPort {

  private final LoanApplicationJpaRepository repository;

  public LoanApplicationRepositoryAdapter(LoanApplicationJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public LoanApplication save(LoanApplication application) {
    return toDomain(repository.save(toEntity(application)));
  }

  @Override
  public List<LoanApplication> findAllByUserId(UUID userId) {
    return repository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
  }

  @Override
  public List<LoanApplication> findAll() {
    return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
  }

  private LoanApplication toDomain(LoanApplicationJpaEntity entity) {
    return new LoanApplication(
        entity.getId(),
        entity.getUserId(),
        entity.getRequestedAmount(),
        entity.getApprovedAmount(),
        entity.getTermMonths(),
        entity.getAnnualInterestRate(),
        entity.getEstimatedInstallment(),
        entity.getStatus(),
        entity.getRiskSnapshotJson(),
        entity.getCreatedAt());
  }

  private LoanApplicationJpaEntity toEntity(LoanApplication application) {
    LoanApplicationJpaEntity entity = new LoanApplicationJpaEntity();
    entity.setId(application.id());
    entity.setUserId(application.userId());
    entity.setRequestedAmount(application.requestedAmount());
    entity.setApprovedAmount(application.approvedAmount());
    entity.setTermMonths(application.termMonths());
    entity.setAnnualInterestRate(application.annualInterestRate());
    entity.setEstimatedInstallment(application.estimatedInstallment());
    entity.setStatus(application.status());
    entity.setRiskSnapshotJson(application.riskSnapshotJson());
    entity.setCreatedAt(application.createdAt());
    return entity;
  }
}
