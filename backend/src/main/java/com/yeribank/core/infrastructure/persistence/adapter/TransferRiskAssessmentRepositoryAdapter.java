package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.TransferRiskAssessmentRepositoryPort;
import com.yeribank.core.domain.model.TransferRiskAssessment;
import com.yeribank.core.infrastructure.persistence.entity.TransferRiskAssessmentJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.TransferRiskAssessmentJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TransferRiskAssessmentRepositoryAdapter implements TransferRiskAssessmentRepositoryPort {

  private final TransferRiskAssessmentJpaRepository repository;

  public TransferRiskAssessmentRepositoryAdapter(TransferRiskAssessmentJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public TransferRiskAssessment save(TransferRiskAssessment assessment) {
    return toDomain(repository.save(toEntity(assessment)));
  }

  @Override
  public Optional<TransferRiskAssessment> findByTransferId(UUID transferId) {
    return repository.findByTransferId(transferId).map(this::toDomain);
  }

  private TransferRiskAssessment toDomain(TransferRiskAssessmentJpaEntity entity) {
    return new TransferRiskAssessment(
        entity.getId(),
        entity.getTransferId(),
        entity.getUserId(),
        entity.getScore(),
        entity.getDecision(),
        entity.getRiskLevel(),
        entity.getReasonsJson(),
        entity.getCreatedAt());
  }

  private TransferRiskAssessmentJpaEntity toEntity(TransferRiskAssessment assessment) {
    TransferRiskAssessmentJpaEntity entity = new TransferRiskAssessmentJpaEntity();
    entity.setId(assessment.id());
    entity.setTransferId(assessment.transferId());
    entity.setUserId(assessment.userId());
    entity.setScore(assessment.score());
    entity.setDecision(assessment.decision());
    entity.setRiskLevel(assessment.riskLevel());
    entity.setReasonsJson(assessment.reasonsJson());
    entity.setCreatedAt(assessment.createdAt());
    return entity;
  }
}
