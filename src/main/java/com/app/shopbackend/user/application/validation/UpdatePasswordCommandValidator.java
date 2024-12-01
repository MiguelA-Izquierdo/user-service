package com.app.shopbackend.user.application.validation;


import com.app.shopbackend._shared.infraestructure.ValidationError;
import com.app.shopbackend.user.application.bus.command.UpdatePasswordCommand;
import com.app.shopbackend.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;


@Component
public class UpdatePasswordCommandValidator {

  public void validate(UpdatePasswordCommand command) {
    ValidationError validationError = new ValidationError();

    try {
      UserId.of(command.id());
    } catch (Exception e) {
      validationError.addError("UserId", e.getMessage());
    }

    try {
      Password.of(command.newPassword());
    } catch (Exception e) {
      validationError.addError("NewPassword", e.getMessage());
    }

    try {
      Password.of(command.currentPassword());
    } catch (Exception e) {
      validationError.addError("CurrentPassword", e.getMessage());
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }

  }

}
