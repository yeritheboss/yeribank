package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {
  RefreshToken save(RefreshToken refreshToken);

  Optional<RefreshToken> findByToken(String token);
}
