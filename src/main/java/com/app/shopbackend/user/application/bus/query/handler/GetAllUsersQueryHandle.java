package com.app.shopbackend.user.application.bus.query.handler;

import com.app.shopbackend._shared.bus.query.QueryHandler;
import com.app.shopbackend.user.application.bus.query.GetAllUsersQuery;
import com.app.shopbackend.user.application.useCases.GetAllUsersUseCase;
import com.app.shopbackend.user.application.validation.GetAllUsersQueryValidator;
import com.app.shopbackend.user.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllUsersQueryHandle implements QueryHandler<GetAllUsersQuery, List<User>> {

  private final GetAllUsersUseCase getAllUsersUseCase;
  private final GetAllUsersQueryValidator getAllUsersQueryValidator;
  public GetAllUsersQueryHandle(GetAllUsersUseCase getAllUsersUseCase,
                                GetAllUsersQueryValidator getAllUsersQueryValidator){
    this.getAllUsersUseCase = getAllUsersUseCase;
    this.getAllUsersQueryValidator = getAllUsersQueryValidator;
  }

  @Override
  public List<User> handle(GetAllUsersQuery query) {
    getAllUsersQueryValidator.validate(query);
    return getAllUsersUseCase.execute(query);
  }
}
