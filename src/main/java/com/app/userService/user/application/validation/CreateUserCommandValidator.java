package com.app.userService.user.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

@Component
public class CreateUserCommandValidator {

  public void validate(CreateUserCommand command) {
    ValidationError validationError = new ValidationError();

    validationError.validate("User Id", () -> UserId.of(command.id()));
    validationError.validate("User name", () -> UserName.of(command.name()));
    validationError.validate("Last name", () -> UserLastName.of(command.lastName()));
    validationError.validate("Email", () -> UserEmail.of(command.email()));
    validationError.validate("Phone", () -> Phone.of(command.countryCode(), command.number()));
    validationError.validate("Identity document", () -> IdentityDocument.of(command.documentType(), command.documentNumber()));
    validationError.validate("Address", () -> Address.of(command.street(), command.streetNumber(), command.city(), command.state(), command.postalCode(), command.country()));
    validationError.validate("Password", () -> Password.of(command.password()));

    if (validationError.hasErrors()) {
      throw validationError;
    }
  }
}