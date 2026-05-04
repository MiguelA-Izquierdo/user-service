package com.app.userService.auth.application.validation;


import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.user.domain.valueObjects.Password;
import org.springframework.stereotype.Component;

@Component
public class UnlockResetPasswordCommandValidator {

  public void validate(UnlockResetPasswordCommand command) {
    ValidationError validationError = new ValidationError();

    validationError.validate("New password", () -> Password.of(command.newPassword()));

    if (command.token() == null) {
      validationError.addError("Token", "token", "Token is required.");
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }
  }

}
