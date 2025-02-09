package com.app.userService.user.application.validation;


import com.app.userService._shared.infraestructure.ValidationError;
import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.domain.valueObjects.*;
import com.app.userService.user.infrastructure.messaging.outbound.UserEventRabbitPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateUserCommandValidator {
  private static final Logger logger = LoggerFactory.getLogger(CreateUserCommandValidator.class);

  public void validate(CreateUserCommand command) {
    ValidationError validationError = new ValidationError();

    logger.info("el comando {}", command);

    validationError.validateField("User Id", command.getUserIdMap(), UserId::getValidationErrors, true);
    validationError.validateField("User name", command.getUserNameMap(), UserName::getValidationErrors, true);
    validationError.validateField("Last name", command.getLastNameMap(), UserLastName::getValidationErrors, true);
    validationError.validateField("Email", command.getEmailMap(), UserEmail::getValidationErrors, true);
    validationError.validateField("Phone", command.getPhoneMap(), Phone::getValidationErrors, true);
    validationError.validateField("Identity document", command.getIdentityDocumentMap(), IdentityDocument::getValidationErrors, true);
    validationError.validateField("Address", command.getAddressMap(), Address::getValidationErrors, true);
    validationError.validateField("Password", command.getPasswordMap(), Password::getValidationErrors, true);


    if (validationError.hasErrors()) {
      throw validationError;
    }

  }

}
