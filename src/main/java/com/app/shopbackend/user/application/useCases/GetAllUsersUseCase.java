package com.app.shopbackend.user.application.useCases;

import com.app.shopbackend.user.application.bus.query.GetAllUsersQuery;
import com.app.shopbackend.user.application.service.UserService;
import com.app.shopbackend.user.domain.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllUsersUseCase {
  private static final Logger logger = LoggerFactory.getLogger(GetAllUsersUseCase.class);

  private final UserService userService;
  public GetAllUsersUseCase(UserService userService){
    this.userService = userService;
  }

  public List<User> execute(GetAllUsersQuery getAllUsersQuery) {

    return userService.findAll(getAllUsersQuery.page(), getAllUsersQuery.size());
  }
}
