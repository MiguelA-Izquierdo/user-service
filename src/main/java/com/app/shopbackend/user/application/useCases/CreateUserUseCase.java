package com.app.shopbackend.user.application.useCases;

import com.app.shopbackend.user.application.service.UserService;
import com.app.shopbackend.user.domain.model.User;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class CreateUserUseCase {

  private final UserService userService;
  public CreateUserUseCase(UserService userService){
    this.userService = userService;
  }
  @Transactional
  public void execute(User user) {
    userService.createUser(user);
  }
}
