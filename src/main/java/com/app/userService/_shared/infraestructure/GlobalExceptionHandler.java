package com.app.userService._shared.infraestructure;

import com.app.userService._shared.exceptions.InvalidPasswordException;
import com.app.userService._shared.exceptions.JwtValidationError;
import com.app.userService._shared.infraestructure.dto.ErrorResponseDTO;
import com.app.userService.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponseDTO> handleNullPointerException(NullPointerException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      "A null value was encountered"
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex) {
//    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
//      HttpStatus.INTERNAL_SERVER_ERROR.value(),
//      "Internal server error"
//    );
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      ex.getMessage()
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


  @ExceptionHandler(ValueObjectValidationException.class)
  public ResponseEntity<ErrorResponseDTO> handleValueObjectValidationException(ValueObjectValidationException ex) {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.UNPROCESSABLE_ENTITY.value(),
      "The provided data is invalid. Please check the fields and try again."
    );

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }

}

