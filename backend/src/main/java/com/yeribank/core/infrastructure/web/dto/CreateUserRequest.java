package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.Role;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank @Email(message = "Invalid email") String email,
    @NotBlank @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password,
    Role role,
    @Size(max = 160, message = "Full name must not exceed 160 characters") String fullName,
    @Min(value = 0, message = "Age cannot be negative") @Max(value = 130, message = "Age is not valid")
        Integer age,
    @Size(max = 120, message = "Job title must not exceed 120 characters") String jobTitle) {}
