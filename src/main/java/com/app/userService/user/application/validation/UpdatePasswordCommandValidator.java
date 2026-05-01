package com.app.userService.user.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.domain.valueObjects.Password;
import com.app.userService.user.domain.valueObjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class UpdatePasswordCommandValidator {

  public void validate(UpdatePasswordCommand command) {
    ValidationError validationError = new ValidationError();

    validationError.validate("User Id", () -> UserId.of(command.id()));
    validationError.validate("Current password", () -> Password.of(command.currentPassword()));
    validationError.validate("New password", () -> Password.of(command.newPassword()));

    if (validationError.hasErrors()) {
      throw validationError;
    }
  }
}