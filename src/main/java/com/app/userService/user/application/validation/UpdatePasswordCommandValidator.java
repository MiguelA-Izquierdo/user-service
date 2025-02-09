package com.app.userService.user.application.validation;


import com.app.userService._shared.infraestructure.ValidationError;
import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;


@Component
public class UpdatePasswordCommandValidator {

  public void validate(UpdatePasswordCommand command) {
    ValidationError validationError = new ValidationError();

    validationError.validateField("User Id", command.getUserIdMap(), UserId::getValidationErrors, true);
    validationError.validateField("Current password", command.getCurrentPasswordMap(), Password::getValidationErrors, true);
    validationError.validateField("New password", command.getNewPasswordMap(), Password::getValidationErrors, true);

    if (validationError.hasErrors()) {
      throw validationError;
    }

  }

}
