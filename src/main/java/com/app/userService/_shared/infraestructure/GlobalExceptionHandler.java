package com.app.userService._shared.infraestructure;

import com.app.userService._shared.exceptions.InvalidPasswordException;
import com.app.userService._shared.exceptions.JwtValidationError;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.put("message", "A null value was encountered");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.put("message", "Internal server error");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
  @ExceptionHandler(ValidationError.class)
  public ResponseEntity<Map<String, Object>> handleValidationError(ValidationError ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("message", "Validation failed");
    errorResponse.put("errors", ex.getErrors());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.NOT_FOUND.value());
    errorResponse.put("message", "Entity not found");
    errorResponse.put("details", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.CONFLICT.value());
    errorResponse.put("message", "User already exists");
    errorResponse.put("details", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidPasswordException(InvalidPasswordException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.FORBIDDEN.value());
    errorResponse.put("message", "Invalid password");
    errorResponse.put("details", ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(JwtValidationError.class)
  public ResponseEntity<Map<String, Object>> handleJwtValidationError(JwtValidationError ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.UNAUTHORIZED.value());
    errorResponse.put("message", "JWT validation failed");
    errorResponse.put("errors", ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(ValueObjectValidationException.class)
  public ResponseEntity<Map<String, Object>> handleValueObjectValidationException(ValueObjectValidationException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.UNPROCESSABLE_ENTITY.value());
    errorResponse.put("message", "The provided data is invalid. Please check the fields and try again.");

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }
}

