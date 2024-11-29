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

    if (!isValidPassword(command.newPassword())) {
      validationError.addError(
        "Password",
        "Password must be at least 9 characters long, include one uppercase letter, and one special character."
      );
    }

    if (command.currentPassword() == null || command.currentPassword().isEmpty()) {
      validationError.addError("CurrentPassword", "Current password cannot be null or empty.");
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }

  }

  private boolean isValidPassword(String password) {
    if (password == null || password.isEmpty()) {
      return false;
    }

    String passwordRegex = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{9,}$";
    return password.matches(passwordRegex);
  }

}
