package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.RiskProfile;
import java.util.Optional;
import java.util.UUID;

public interface RiskProfileRepositoryPort {
  RiskProfile save(RiskProfile profile);

  Optional<RiskProfile> findByUserId(UUID userId);
}
