package com.app.userService.user.application.bus.query.handler;

import com.app.userService._shared.application.bus.query.QueryHandler;
import com.app.userService.user.application.bus.query.GetUserByIdQuery;
import com.app.userService.user.application.useCases.GetUserByIdUseCase;
import com.app.userService.user.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserByIdQueryHandle implements QueryHandler<GetUserByIdQuery, Optional<User>> {

  private final GetUserByIdUseCase getUserByIdUseCase;
  public GetUserByIdQueryHandle(GetUserByIdUseCase getUserByIdUseCase){
    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  @Override
  public Optional<User> handle(GetUserByIdQuery query) {
    return getUserByIdUseCase.execute(query);
  }
}
