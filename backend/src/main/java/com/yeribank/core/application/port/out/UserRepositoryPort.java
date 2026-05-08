package com.yeribank.core.application.port.out;

import com.yeribank.core.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
  Optional<User> findByEmail(String email);

  Optional<User> findById(UUID id);

  boolean existsByEmail(String email);

  long countUsers();

  List<User> findAll();

  User save(User user);
}
