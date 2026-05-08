package com.yeribank.core.domain.model;

import com.yeribank.core.domain.model.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public record User(
    UUID id,
    String email,
    String passwordHash,
    Role role,
    String fullName,
    Integer age,
    String jobTitle,
    LocalDateTime createdAt) {

  public static User create(
      UUID id,
      String email,
      String passwordHash,
      Role role,
      String fullName,
      Integer age,
      String jobTitle) {
    return new User(id, email, passwordHash, role, fullName, age, jobTitle, LocalDateTime.now());
  }
}
