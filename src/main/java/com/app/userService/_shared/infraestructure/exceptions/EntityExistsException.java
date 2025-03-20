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

public class EntityExistsException extends RuntimeException {
  public EntityExistsException(String message) {
    super(message);
  }


}
