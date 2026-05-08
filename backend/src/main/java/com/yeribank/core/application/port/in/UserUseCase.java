package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.CreateUserCommand;
import com.yeribank.core.application.dto.ListUsersQuery;
import com.yeribank.core.application.dto.UserResult;
import java.util.List;

public interface UserUseCase {
  UserResult create(CreateUserCommand command);

  List<UserResult> list(ListUsersQuery query);
}
