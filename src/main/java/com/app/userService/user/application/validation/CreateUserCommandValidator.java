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
    try {
      UserId.of(command.id());
    } catch (Exception e) {
      validationError.addError("id", e.getMessage());
    }

    try {
      UserName.of(command.name());
    } catch (Exception e) {
      validationError.addError("name", e.getMessage());
    }

    try {
      UserLastName.of(command.lastName());
    } catch (Exception e) {
      validationError.addError("Last Name", e.getMessage());
    }

    try {
      UserEmail.of(command.email());
    } catch (Exception e) {
      validationError.addError("Email", e.getMessage());
    }

    try {
      Phone.of(command.countryCode(), command.number());
    } catch (Exception e) {
      validationError.addError("Phone", e.getMessage());
    }

    try {
      IdentityDocument.of(command.documentType(), command.documentNumber());
    } catch (Exception e) {
      validationError.addError("Identity Document", e.getMessage());
    }

    try {
      Address.of(
        command.street(),
        command.streetNumber(),
        command.city(),
        command.state(),
        command.postalCode(),
        command.country());
    } catch (Exception e) {
      validationError.addError("Address", e.getMessage());
    }

    try {
     Password.of(command.password());
    } catch (Exception e) {
      validationError.addError("Password", e.getMessage());
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }




  }

}
