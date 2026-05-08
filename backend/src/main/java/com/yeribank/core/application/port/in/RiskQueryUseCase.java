package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.GetRiskProfileQuery;
import com.yeribank.core.application.dto.GetTransferRiskAssessmentQuery;
import com.yeribank.core.application.dto.RiskProfileResult;
import com.yeribank.core.application.dto.TransferRiskAssessmentResult;

public interface RiskQueryUseCase {
  RiskProfileResult getProfile(GetRiskProfileQuery query);

  TransferRiskAssessmentResult getTransferAssessment(GetTransferRiskAssessmentQuery query);
}
