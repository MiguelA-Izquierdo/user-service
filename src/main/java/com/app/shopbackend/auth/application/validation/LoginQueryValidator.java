package com.app.shopbackend.auth.application.validation;


import com.app.shopbackend._shared.infraestructure.ValidationError;
import com.app.shopbackend.auth.application.bus.query.LoginQuery;
import com.app.shopbackend.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

@Component
public class LoginQueryValidator {

  public void validate(LoginQuery query) {
    ValidationError validationError = new ValidationError();

    try {
      UserEmail.of(query.email());
    } catch (Exception e) {
      validationError.addError("Email", e.getMessage());
    }

    if (!isValidPassword(query.password())) {
      validationError.addError(
        "Password",
        "Password must be at least 9 characters long, include one uppercase letter, and one special character."
      );
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
