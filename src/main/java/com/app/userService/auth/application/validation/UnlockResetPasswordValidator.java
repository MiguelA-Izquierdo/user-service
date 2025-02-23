package com.app.userService.auth.application.validation;


import com.app.userService._shared.infraestructure.ValidationError;
import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.user.domain.valueObjects.Password;
import com.app.userService.user.domain.valueObjects.UserEmail;
import org.springframework.stereotype.Component;

@Component
public class UnlockResetPasswordValidator {

  public void validate(UnlockResetPasswordCommand command) {
    ValidationError validationError = new ValidationError();

    try {
      Password.of(command.newPassword());
    } catch (Exception e) {
      validationError.addError("NewPassword","Password", e.getMessage());
    }

    if(command.token() == null){
      validationError.addError("Token","token", "Token is required.");
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }

  }

}
