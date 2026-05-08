package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.FinancialProfileRepositoryPort;
import com.yeribank.core.domain.model.FinancialProfile;
import com.yeribank.core.infrastructure.persistence.entity.FinancialProfileJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.FinancialProfileJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FinancialProfileRepositoryAdapter implements FinancialProfileRepositoryPort {

  private final FinancialProfileJpaRepository repository;

  public FinancialProfileRepositoryAdapter(FinancialProfileJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public FinancialProfile save(FinancialProfile profile) {
    return toDomain(repository.save(toEntity(profile)));
  }

  @Override
  public Optional<FinancialProfile> findByUserId(UUID userId) {
    return repository.findByUserId(userId).map(this::toDomain);
  }

  private FinancialProfile toDomain(FinancialProfileJpaEntity entity) {
    return new FinancialProfile(
        entity.getId(),
        entity.getUserId(),
        entity.getMonthlyIncome(),
        entity.getMonthlyExpenses(),
        entity.getCurrentDebt(),
        entity.getUpdatedAt());
  }

  private FinancialProfileJpaEntity toEntity(FinancialProfile profile) {
    FinancialProfileJpaEntity entity = new FinancialProfileJpaEntity();
    entity.setId(profile.id());
    entity.setUserId(profile.userId());
    entity.setMonthlyIncome(profile.monthlyIncome());
    entity.setMonthlyExpenses(profile.monthlyExpenses());
    entity.setCurrentDebt(profile.currentDebt());
    entity.setUpdatedAt(profile.updatedAt());
    return entity;
  }
}
