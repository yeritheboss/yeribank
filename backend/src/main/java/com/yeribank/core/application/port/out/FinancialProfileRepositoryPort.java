package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.FinancialProfile;
import java.util.Optional;
import java.util.UUID;

public interface FinancialProfileRepositoryPort {
  FinancialProfile save(FinancialProfile profile);

  Optional<FinancialProfile> findByUserId(UUID userId);
}
