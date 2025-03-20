package com.app.userService._shared.infraestructure.exceptions;

import com.app.userService._shared.infraestructure.ValidationError;
import com.app.userService._shared.infraestructure.dto.ErrorResponseDTO;
import com.app.userService.auth.domain.exceptions.TokenExpiredException;
import com.app.userService.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponseDTO> handleNullPointerException(NullPointerException ex) {
    String message =  "A null value was encountered";
    String messageDetails = ex.getMessage();

    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
  @ExceptionHandler(EntityExistsException.class)
  public ResponseEntity<ErrorResponseDTO> handleEntityExistsException(EntityExistsException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.CONFLICT.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex) {
    String message =  "Internal server error";
    String messageDetails = ex.getMessage();

    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      messageDetails
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
  @ExceptionHandler(ValidationError.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationError(ValidationError ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.BAD_REQUEST.value(),
      "Validation failed",
      ex.getErrors()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(EntityNotFoundException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.NOT_FOUND.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.CONFLICT.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(RoleAlreadyGrantedException.class)
  public ResponseEntity<ErrorResponseDTO> handleUserHasRole(RoleAlreadyGrantedException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.CONFLICT.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidPasswordException(InvalidPasswordException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.UNAUTHORIZED.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(UserLockedException.class)
  public ResponseEntity<ErrorResponseDTO> handleUserLockedException(UserLockedException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.FORBIDDEN.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(JwtValidationError.class)
  public ResponseEntity<ErrorResponseDTO> handleJwtValidationError(JwtValidationError ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.UNAUTHORIZED.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.FORBIDDEN.value(),
      "Access denied: You do not have the required permissions to perform this action."
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }
  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<ErrorResponseDTO> handleTokenExpiredException(TokenExpiredException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.BAD_REQUEST.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }


  @ExceptionHandler(ValueObjectValidationException.class)
  public ResponseEntity<ErrorResponseDTO> handleValueObjectValidationException(ValueObjectValidationException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.UNPROCESSABLE_ENTITY.value(),
      ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }

}
