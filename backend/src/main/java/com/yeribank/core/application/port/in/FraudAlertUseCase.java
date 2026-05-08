package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.FraudAlertResult;
import com.yeribank.core.application.dto.ListFraudAlertsQuery;
import com.yeribank.core.application.dto.ReviewFraudAlertCommand;
import java.util.List;

public interface FraudAlertUseCase {
  List<FraudAlertResult> listAlerts(ListFraudAlertsQuery query);

  FraudAlertResult reviewAlert(ReviewFraudAlertCommand command);
}
