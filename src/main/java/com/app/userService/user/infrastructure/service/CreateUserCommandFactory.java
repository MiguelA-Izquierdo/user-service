package com.app.userService.user.infrastructure.service;

import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.infrastructure.api.dto.CreateUserCommandDTO;
import org.springframework.stereotype.Component;

@Component
public class CreateUserCommandFactory {
  public CreateUserCommand create(CreateUserCommandDTO createUserCommandDTO) {

    return new CreateUserCommand(
      createUserCommandDTO.id(),
      createUserCommandDTO.name(),
      createUserCommandDTO.lastName(),
      createUserCommandDTO.email(),
      createUserCommandDTO.password(),
      createUserCommandDTO.countryCode(),
      createUserCommandDTO.number(),
      createUserCommandDTO.documentType(),
      createUserCommandDTO.documentNumber(),
      createUserCommandDTO.street(),
      createUserCommandDTO.streetNumber(),
      createUserCommandDTO.city(),
      createUserCommandDTO.state(),
      createUserCommandDTO.postalCode(),
      createUserCommandDTO.country()
    );
  }
}

