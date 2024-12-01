package com.app.shopbackend.user.application.validation;


import com.app.shopbackend._shared.infraestructure.ValidationError;
import com.app.shopbackend.user.application.bus.command.CreateUserCommand;
import com.app.shopbackend.user.domain.model.Role;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreateUserCommandValidator {

  public void validate(CreateUserCommand command) {
    ValidationError validationError = new ValidationError();

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
