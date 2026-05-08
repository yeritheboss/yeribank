package com.yeribank.core.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank @Email(message = "Invalid email") String email,
    @NotBlank(message = "Password is required") String password) {}
