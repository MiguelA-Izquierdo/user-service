package com.app.userService.user.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserCommandValidator {

  public void validate(UpdateUserCommand command) {
    ValidationError validationError = new ValidationError();

    boolean hasAtLeastOneField =
      command.userName() != null ||
      command.lastName() != null ||
      command.identityDocument() != null ||
      command.address() != null ||
      command.phone() != null;

    if (!hasAtLeastOneField) {
      validationError.addError("Invalid request", "Fields", "You must provide at least one primary field to update.");
      throw validationError;
    }

    validationError.validate("User Id", () -> UserId.of(command.userId()));

    if (command.userName() != null)
      validationError.validate("User name", () -> UserName.of(command.userName()));

    if (command.lastName() != null)
      validationError.validate("Last name", () -> UserLastName.of(command.lastName()));

    if (command.identityDocument() != null)
      validationError.validate("Identity document", () ->
        IdentityDocument.of(command.identityDocument().documentType(), command.identityDocument().documentNumber()));

    if (command.phone() != null)
      validationError.validate("Phone", () ->
        Phone.of(command.phone().countryCode(), command.phone().phoneNumber()));

    if (command.address() != null)
      validationError.validate("Address", () ->
        Address.of(command.address().street(), command.address().streetNumber(),
          command.address().city(), command.address().state(),
          command.address().postalCode(), command.address().country()));

    if (validationError.hasErrors()) {
      throw validationError;
    }
  }
}