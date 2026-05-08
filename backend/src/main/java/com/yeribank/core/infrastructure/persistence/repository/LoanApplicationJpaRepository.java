package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.LoanApplicationJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationJpaRepository extends JpaRepository<LoanApplicationJpaEntity, UUID> {
  List<LoanApplicationJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

  List<LoanApplicationJpaEntity> findAllByOrderByCreatedAtDesc();
}
