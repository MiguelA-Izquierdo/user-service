package com.app.shopbackend.user.application.useCases;

import com.app.shopbackend.user.application.bus.query.GetUserByIdQuery;
import com.app.shopbackend.user.application.service.UserService;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.valueObjects.UserId;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;
@Service
public class GetUserByIdUseCase {
  private final UserService userService;
  public GetUserByIdUseCase(UserService userService){
    this.userService = userService;
  }
  @Transactional
  public Optional<User> execute(GetUserByIdQuery getUserByIdQuery) {
    return userService.findUserById(UserId.of(getUserByIdQuery.getId()));
  }
}
