package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.FinancialProfileCommand;
import com.yeribank.core.application.dto.FinancialProfileResult;
import com.yeribank.core.application.dto.GetFinancialProfileQuery;

public interface FinancialProfileUseCase {
  FinancialProfileResult upsert(FinancialProfileCommand command);

  FinancialProfileResult get(GetFinancialProfileQuery query);
}
