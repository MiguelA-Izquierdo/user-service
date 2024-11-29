package com.app.shopbackend.user.application.bus.query.handler;

import com.app.shopbackend._shared.bus.query.QueryHandler;
import com.app.shopbackend.user.application.bus.query.GetUserByIdQuery;
import com.app.shopbackend.user.application.useCases.GetUserByIdUseCase;
import com.app.shopbackend.user.domain.model.User;
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
