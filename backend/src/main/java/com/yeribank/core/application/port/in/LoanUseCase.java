package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.CreateLoanApplicationCommand;
import com.yeribank.core.application.dto.ListLoanApplicationsQuery;
import com.yeribank.core.application.dto.LoanApplicationResult;
import com.yeribank.core.application.dto.LoanOfferResult;
import com.yeribank.core.application.dto.LoanSimulationCommand;
import java.util.List;

public interface LoanUseCase {
  LoanOfferResult simulate(LoanSimulationCommand command);

  LoanApplicationResult createApplication(CreateLoanApplicationCommand command);

  List<LoanApplicationResult> listApplications(ListLoanApplicationsQuery query);
}
