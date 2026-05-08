package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.TransferRiskAssessment;
import java.util.Optional;
import java.util.UUID;

public interface TransferRiskAssessmentRepositoryPort {
  TransferRiskAssessment save(TransferRiskAssessment assessment);

  Optional<TransferRiskAssessment> findByTransferId(UUID transferId);
}
