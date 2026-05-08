package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.AccountResult;
import com.yeribank.core.application.dto.BalanceResult;
import com.yeribank.core.application.dto.CreateAccountCommand;
import com.yeribank.core.application.dto.GetBalanceQuery;
import com.yeribank.core.application.dto.ListAccountsQuery;
import java.util.List;

public interface AccountUseCase {
  AccountResult create(CreateAccountCommand command);

  List<AccountResult> list(ListAccountsQuery query);

  BalanceResult getBalance(GetBalanceQuery query);
}
