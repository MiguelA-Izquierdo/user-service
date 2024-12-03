package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.query.GetUserByIdQuery;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;
@Service
public class GetUserByIdUseCase {
  private final UserServiceCore userServiceCore;
  public GetUserByIdUseCase(UserServiceCore userServiceCore){
    this.userServiceCore = userServiceCore;
  }
  @Transactional
  public Optional<User> execute(GetUserByIdQuery getUserByIdQuery) {
    return userServiceCore.findUserById(UserId.of(getUserByIdQuery.getId())).getUser();
  }
}
