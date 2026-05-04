package com.app.userService.auth.application.validation;


import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

@Component
public class LoginQueryValidator {

  public void validate(LoginQuery query) {
    ValidationError validationError = new ValidationError();

    validationError.validate("Email", () -> UserEmail.of(query.email()));
    validationError.validate("Password", () -> Password.of(query.password()));

    if (validationError.hasErrors()) {
      throw validationError;
    }
  }
}
