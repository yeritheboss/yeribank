package com.yeribank.core.application.dto;

import com.yeribank.core.domain.model.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResult(
    UUID id,
    String email,
    Role role,
    String fullName,
    Integer age,
    String jobTitle,
    LocalDateTime createdAt) {}
