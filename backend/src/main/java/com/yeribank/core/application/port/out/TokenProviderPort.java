package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.User;

public interface TokenProviderPort {
  String generateAccessToken(User user);

  long getAccessTokenExpirationSeconds();
}
