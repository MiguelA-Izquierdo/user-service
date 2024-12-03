package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.query.GetAllUsersQuery;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetAllUsersUseCase {
  private static final Logger logger = LoggerFactory.getLogger(GetAllUsersUseCase.class);

  private final UserServiceCore userServiceCore;
  public GetAllUsersUseCase(UserServiceCore userServiceCore){
    this.userServiceCore = userServiceCore;
  }

  public PaginatedResult<User> execute(GetAllUsersQuery getAllUsersQuery) {

    return userServiceCore.findAll(getAllUsersQuery.page(), getAllUsersQuery.size());
  }
}
