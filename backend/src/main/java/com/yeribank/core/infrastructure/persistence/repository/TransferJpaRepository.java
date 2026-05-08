package com.yeribank.core.infrastructure.persistence.repository;

import com.yeribank.core.infrastructure.persistence.entity.TransferJpaEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransferJpaRepository extends JpaRepository<TransferJpaEntity, UUID> {
  long countByFromAccountIdAndCreatedAtAfter(UUID fromAccountId, LocalDateTime threshold);

  long countByFromAccountIdAndToAccountId(UUID fromAccountId, UUID toAccountId);

  List<TransferJpaEntity> findByFromAccountIdAndCreatedAtAfter(UUID fromAccountId, LocalDateTime threshold);

  @Query(
      """
      select t from TransferJpaEntity t
      where t.fromAccountId = :accountId or t.toAccountId = :accountId
      order by t.createdAt desc
      """)
  List<TransferJpaEntity> findRecentByAccountId(UUID accountId, Pageable pageable);

  @Query(
      """
      select t from TransferJpaEntity t
      where t.fromAccountId in :accountIds or t.toAccountId in :accountIds
      order by t.createdAt desc
      """)
  List<TransferJpaEntity> findRecentByAccountIds(List<UUID> accountIds, Pageable pageable);
}
