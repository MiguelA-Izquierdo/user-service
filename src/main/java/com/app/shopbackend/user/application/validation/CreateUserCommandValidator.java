package com.app.shopbackend.user.application.validation;


import com.app.shopbackend._shared.infraestructure.ValidationError;
import com.app.shopbackend.user.application.bus.command.CreateUserCommand;
import com.app.shopbackend.user.domain.model.Role;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.valueObjects.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreateUserCommandValidator {

  public User validate(CreateUserCommand command) {
    ValidationError validationError = new ValidationError();

    UserId userId = null;
    try {
      userId = UserId.of(command.id());
    } catch (Exception e) {
      validationError.addError("id", e.getMessage());
    }

    UserName userName = null;
    try {
      userName = UserName.of(command.name());
    } catch (Exception e) {
      validationError.addError("name", e.getMessage());
    }

    UserLastName userLastName = null;
    try {
      userLastName = UserLastName.of(command.lastName());
    } catch (Exception e) {
      validationError.addError("Last Name", e.getMessage());
    }

    UserEmail userEmail = null;
    try {
      userEmail = UserEmail.of(command.email());
    } catch (Exception e) {
      validationError.addError("Email", e.getMessage());
    }

    Phone phone = null;
    try {
      phone = Phone.of(command.countryCode(), command.number());
    } catch (Exception e) {
      validationError.addError("Phone", e.getMessage());
    }

    IdentityDocument identityDocument = null;
    try {
      identityDocument = IdentityDocument.of(command.documentType(), command.documentNumber());
    } catch (Exception e) {
      validationError.addError("Identity Document", e.getMessage());
    }

    Address address = null;
    try {
      address = Address.of(
        command.street(),
        command.streetNumber(),
        command.city(),
        command.state(),
        command.postalCode(),
        command.country());
    } catch (Exception e) {
      validationError.addError("Address", e.getMessage());
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }

    List<Role> emptyRolesList = new ArrayList<>();
    User user = new User(userId, userName, userLastName, userEmail, identityDocument, phone, address, command.password(), emptyRolesList);

    return user;
  }

}
