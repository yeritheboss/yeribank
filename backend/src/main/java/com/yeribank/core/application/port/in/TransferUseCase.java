package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.ExecuteTransferCommand;
import com.yeribank.core.application.dto.AccountTransferResult;
import com.yeribank.core.application.dto.ListAccountTransfersQuery;
import com.yeribank.core.application.dto.TransferResult;
import java.util.List;

public interface TransferUseCase {
  TransferResult execute(ExecuteTransferCommand command);

  List<AccountTransferResult> listAccountTransfers(ListAccountTransfersQuery query);
}
