package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.AccountJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, UUID> {
  boolean existsByAccountNumber(String accountNumber);

  Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);

  List<AccountJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

  List<AccountJpaEntity> findAllByOrderByCreatedAtDesc();
}
