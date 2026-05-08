package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.LoanApplication;
import java.util.List;
import java.util.UUID;

public interface LoanApplicationRepositoryPort {
  LoanApplication save(LoanApplication application);

  List<LoanApplication> findAllByUserId(UUID userId);

  List<LoanApplication> findAll();
}
