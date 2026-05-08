package com.yeribank.core.infrastructure.web;

import com.yeribank.core.application.dto.AuthTokensResult;
import com.yeribank.core.application.dto.LoginCommand;
import com.yeribank.core.application.dto.RefreshCommand;
import com.yeribank.core.application.port.in.AuthUseCase;
import com.yeribank.core.infrastructure.web.dto.AuthTokensResponse;
import com.yeribank.core.infrastructure.web.dto.LoginRequest;
import com.yeribank.core.infrastructure.web.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacion", description = "Login y renovacion de tokens JWT.")
public class AuthController {

  private final AuthUseCase authUseCase;

  public AuthController(AuthUseCase authUseCase) {
    this.authUseCase = authUseCase;
  }

  @PostMapping("/login")
  @Operation(summary = "Iniciar sesion", description = "Devuelve access token y refresh token para un usuario registrado.")
  public ResponseEntity<AuthTokensResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthTokensResult result = authUseCase.login(new LoginCommand(request.email(), request.password()));
    return ResponseEntity.ok(toResponse(result));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Renovar token", description = "Genera nuevos tokens usando un refresh token valido.")
  public ResponseEntity<AuthTokensResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    AuthTokensResult result = authUseCase.refresh(new RefreshCommand(request.refreshToken()));
    return ResponseEntity.ok(toResponse(result));
  }

  private AuthTokensResponse toResponse(AuthTokensResult result) {
    return new AuthTokensResponse(
        result.accessToken(), result.refreshToken(), result.tokenType(), result.expiresInSeconds());
  }
}
