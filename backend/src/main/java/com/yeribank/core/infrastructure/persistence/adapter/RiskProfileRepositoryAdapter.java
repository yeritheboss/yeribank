package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.RiskProfileRepositoryPort;
import com.yeribank.core.domain.model.RiskProfile;
import com.yeribank.core.infrastructure.persistence.entity.RiskProfileJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.RiskProfileJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RiskProfileRepositoryAdapter implements RiskProfileRepositoryPort {

  private final RiskProfileJpaRepository repository;

  public RiskProfileRepositoryAdapter(RiskProfileJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public RiskProfile save(RiskProfile profile) {
    return toDomain(repository.save(toEntity(profile)));
  }

  @Override
  public Optional<RiskProfile> findByUserId(UUID userId) {
    return repository.findByUserId(userId).map(this::toDomain);
  }

  private RiskProfile toDomain(RiskProfileJpaEntity entity) {
    return new RiskProfile(
        entity.getId(),
        entity.getUserId(),
        entity.getScore(),
        entity.getLastAssessmentScore(),
        entity.getRiskLevel(),
        entity.getAlertCount90d(),
        entity.getUpdatedAt());
  }

  private RiskProfileJpaEntity toEntity(RiskProfile profile) {
    RiskProfileJpaEntity entity = new RiskProfileJpaEntity();
    entity.setId(profile.id());
    entity.setUserId(profile.userId());
    entity.setScore(profile.score());
    entity.setLastAssessmentScore(profile.lastAssessmentScore());
    entity.setRiskLevel(profile.riskLevel());
    entity.setAlertCount90d(profile.alertCount90d());
    entity.setUpdatedAt(profile.updatedAt());
    return entity;
  }
}
