package com.yeribank.core.application.service;

import com.yeribank.core.application.dto.CreateUserCommand;
import com.yeribank.core.application.dto.ListUsersQuery;
import com.yeribank.core.application.dto.UserResult;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.UserUseCase;
import com.yeribank.core.application.port.out.UserRepositoryPort;
import com.yeribank.core.domain.model.enums.Role;
import com.yeribank.core.domain.model.User;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserUseCase {

  private final UserRepositoryPort userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuditLogService auditLogService;

  public UserService(
      UserRepositoryPort userRepository,
      PasswordEncoder passwordEncoder,
      AuditLogService auditLogService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.auditLogService = auditLogService;
  }

  @Override
  @Transactional
  public UserResult create(CreateUserCommand command) {
    String normalizedEmail = command.email().trim().toLowerCase();
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new AppException(HttpStatus.CONFLICT, "Email already registered");
    }

    Role targetRole = resolveRequestedRole(command.role());
    User user =
        User.create(
            UUID.randomUUID(),
            normalizedEmail,
            passwordEncoder.encode(command.password()),
            targetRole,
            normalizeBlank(command.fullName()),
            command.age(),
            normalizeBlank(command.jobTitle()));

    User saved = userRepository.save(user);
    auditLogService.record(
        saved.id(),
        "USER_CREATED",
        "USER",
        saved.id().toString(),
        "SUCCESS",
        "{\"email\":\"" + saved.email() + "\",\"role\":\"" + saved.role().name() + "\"}");
    return toResult(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResult> list(ListUsersQuery query) {
    if (query.actorAdmin()) {
      return userRepository.findAll().stream().map(this::toResult).toList();
    }

    return userRepository.findById(query.actorUserId()).stream().map(this::toResult).toList();
  }

  private Role resolveRequestedRole(Role requestedRole) {
    Role role = Objects.requireNonNullElse(requestedRole, Role.USER);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdminCaller =
        authentication != null
            && authentication.isAuthenticated()
            && authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

    if (role == Role.ADMIN && !isAdminCaller && userRepository.countUsers() > 0) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only ADMIN can create ADMIN users");
    }

    return role;
  }

  private UserResult toResult(User user) {
    return new UserResult(
        user.id(),
        user.email(),
        user.role(),
        user.fullName(),
        user.age(),
        user.jobTitle(),
        user.createdAt());
  }

  private String normalizeBlank(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
