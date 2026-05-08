package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.CreateUserCommand;
import com.yeribank.core.application.dto.ListUsersQuery;
import com.yeribank.core.application.dto.UserResult;
import com.yeribank.core.application.port.in.UserUseCase;
import com.yeribank.core.infrastructure.web.dto.CreateUserRequest;
import com.yeribank.core.infrastructure.web.dto.PageResponse;
import com.yeribank.core.infrastructure.web.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Registro y administracion de usuarios.")
public class UserController {

  private final UserUseCase userUseCase;
  private final SecurityContextFacade securityContextFacade;

  public UserController(UserUseCase userUseCase, SecurityContextFacade securityContextFacade) {
    this.userUseCase = userUseCase;
    this.securityContextFacade = securityContextFacade;
  }

  @PostMapping
  @Operation(summary = "Crear usuario", description = "Registra un usuario. El primer admin puede crearse sin autenticacion.")
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
    UserResult user =
        userUseCase.create(
            new CreateUserCommand(
                request.email(),
                request.password(),
                request.role(),
                request.fullName(),
                request.age(),
                request.jobTitle()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
  }

  @GetMapping
  @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios si eres admin; si no, solo el usuario autenticado.")
  public ResponseEntity<PageResponse<UserResponse>> listUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        PageResponse.of(
            userUseCase
                .list(
                    new ListUsersQuery(
                        securityContextFacade.currentUserId(), securityContextFacade.isAdmin()))
                .stream()
                .map(this::toResponse)
                .toList(),
            page,
            size));
  }

  private UserResponse toResponse(UserResult user) {
    return new UserResponse(
        user.id(),
        user.email(),
        user.role(),
        user.fullName(),
        user.age(),
        user.jobTitle(),
        user.createdAt());
  }
}
