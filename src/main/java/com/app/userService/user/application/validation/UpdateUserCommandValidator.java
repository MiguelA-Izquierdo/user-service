package com.app.userService.user.application.validation;

import com.app.userService._shared.infraestructure.ValidationError;
import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserCommandValidator {

  public void validate(UpdateUserCommand command) {
    ValidationError validationError = new ValidationError();

    boolean hasAtLeastOneField =
      hasValue(command.userName()) ||
        hasValue(command.lastName()) ||
        hasValue(command.identityDocument()) ||
        hasValue(command.address()) ||
        hasValue(command.phone());

    if (!hasAtLeastOneField) {
      validationError.addError("Invalid request", "Fields", "You must provide at least one primary field to update.");
      throw validationError;
    }


    validationError.validateField("User Id", command.getUserIdMap(), UserId::getValidationErrors, true);
    validationError.validateField("User name", command.getUserNameMap(), UserName::getValidationErrors, false);
    validationError.validateField("Last name", command.getLastNameMap(), UserLastName::getValidationErrors, false);
    validationError.validateField("Identity document", command.getIdentityDocumentMap(), IdentityDocument::getValidationErrors, false);
    validationError.validateField("Phone", command.getPhoneMap(), Phone::getValidationErrors, false);
    validationError.validateField("Address", command.getAddressMap(), Address::getValidationErrors, false);

    if (validationError.hasErrors()) {
      throw validationError;
    }
  }

  private <T> boolean hasValue(T field) {
    return field != null && !field.toString().isEmpty();
  }
}
