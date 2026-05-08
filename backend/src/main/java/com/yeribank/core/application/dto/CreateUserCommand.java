package com.yeribank.core.application.dto;

import com.yeribank.core.domain.model.enums.Role;

public record CreateUserCommand(
    String email, String password, Role role, String fullName, Integer age, String jobTitle) {}
