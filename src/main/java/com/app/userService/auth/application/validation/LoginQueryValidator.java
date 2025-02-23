package com.app.userService.auth.application.validation;


import com.app.userService._shared.infraestructure.ValidationError;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

@Component
public class LoginQueryValidator {

  public void validate(LoginQuery query) {
    ValidationError validationError = new ValidationError();

    try {
      UserEmail.of(query.email());
    } catch (Exception e) {
      validationError.addError("User email","Email", e.getMessage());
    }

    try {
      Password.of(query.password());
    } catch (Exception e) {
      validationError.addError("Password","Password", e.getMessage());
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }

  }
}
