package com.app.shopbackend.user.application.useCases;

import com.app.shopbackend.user.application.bus.command.CreateUserCommand;
import com.app.shopbackend.user.application.bus.event.UserCreatedEvent;
import com.app.shopbackend.user.application.service.UserService;
import com.app.shopbackend.user.domain.event.UserEvent;
import com.app.shopbackend.user.domain.model.Role;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.service.EventPublisher;
import com.app.shopbackend.user.domain.valueObjects.*;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateUserUseCase {

  private final UserService userService;
  private final EventPublisher userPublisher;
  public CreateUserUseCase(UserService userService, EventPublisher userPublisher){
    this.userService = userService;
    this.userPublisher = userPublisher;
  }
  @Transactional
  public void execute(CreateUserCommand command) {
    User user = createUserFromCommand(command);
    userService.createUser(user);
    UserEvent userEvent = UserCreatedEvent.of(user);
    userPublisher.publish(userEvent);
  }

  private User createUserFromCommand(CreateUserCommand command){
    UserId userId = UserId.of(command.id());
    UserName userName = UserName.of(command.name());
    UserLastName userLastName = userLastName = UserLastName.of(command.lastName());
    UserEmail userEmail = UserEmail.of(command.email());
    Phone phone = Phone.of(command.countryCode(), command.number());
    IdentityDocument identityDocument = IdentityDocument.of(command.documentType(), command.documentNumber());
    Address address = Address.of(
      command.street(),
      command.streetNumber(),
      command.city(),
      command.state(),
      command.postalCode(),
      command.country());

    String passwordHashed = userService.encryptPassword(command.password());

    List<Role> emptyRolesList = new ArrayList<>();
    return new User(userId, userName, userLastName, userEmail, identityDocument, phone, address, passwordHashed, LocalDateTime.now(),emptyRolesList);
  }
}
