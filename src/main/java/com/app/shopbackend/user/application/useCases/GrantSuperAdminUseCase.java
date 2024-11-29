package com.app.shopbackend.user.application.useCases;

import com.app.shopbackend.user.application.bus.command.GrantSuperAdminCommand;
import com.app.shopbackend.user.application.service.UserService;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.valueObjects.UserId;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class GrantSuperAdminUseCase {

  private final UserService userService;
  public GrantSuperAdminUseCase(UserService userService){
    this.userService = userService;
  }
  @Transactional
  public void execute(GrantSuperAdminCommand grantSuperAdminCommand) {
    Optional<User> userOptional = userService.findUserById(UserId.of(grantSuperAdminCommand.id()));

    User user = userOptional.orElseThrow(() ->
      new EntityNotFoundException("User with ID " + grantSuperAdminCommand.id() + " not found")
    );

    userService.grantSuperAdmin(user);
  }
}
