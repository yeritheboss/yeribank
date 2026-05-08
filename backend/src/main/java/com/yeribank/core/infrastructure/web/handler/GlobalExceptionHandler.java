package com.yeribank.core.infrastructure.web.handler;

import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.infrastructure.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
    log.warn(
        "{} {} -> {} {}",
        request.getMethod(),
        request.getRequestURI(),
        ex.getStatus().value(),
        ex.getMessage());
    return build(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .orElse("Validation error");

    log.warn("{} {} -> 400 {}", request.getMethod(), request.getRequestURI(), message);
    return build(HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraint(
      ConstraintViolationException ex, HttpServletRequest request) {
    log.warn("{} {} -> 400 {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
    return build(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ErrorResponse> handleOptimisticLock(
      ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
    log.warn(
        "{} {} -> 409 Concurrent update detected",
        request.getMethod(),
        request.getRequestURI());
    return build(HttpStatus.CONFLICT, "Concurrent update detected. Retry the operation");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.error(
        request.getMethod() + " " + request.getRequestURI() + " -> 500 Unexpected server error",
        ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
  }

  private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(status.value(), message, Instant.now()));
  }
}
