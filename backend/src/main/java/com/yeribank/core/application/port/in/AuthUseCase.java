package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.AuthTokensResult;
import com.yeribank.core.application.dto.LoginCommand;
import com.yeribank.core.application.dto.RefreshCommand;

public interface AuthUseCase {
  AuthTokensResult login(LoginCommand command);

  AuthTokensResult refresh(RefreshCommand command);
}
