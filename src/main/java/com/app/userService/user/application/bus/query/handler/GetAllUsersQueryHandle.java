package com.app.userService.user.application.bus.query.handler;

import com.app.userService._shared.application.bus.query.QueryHandler;
import com.app.userService.user.application.bus.query.GetAllUsersQuery;
import com.app.userService.user.application.useCases.GetAllUsersUseCase;
import com.app.userService.user.application.validation.GetAllUsersQueryValidator;
import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;
import org.springframework.stereotype.Service;


@Service
public class GetAllUsersQueryHandle implements QueryHandler<GetAllUsersQuery, PaginatedResult<User>> {

  private final GetAllUsersUseCase getAllUsersUseCase;
  private final GetAllUsersQueryValidator getAllUsersQueryValidator;
  public GetAllUsersQueryHandle(GetAllUsersUseCase getAllUsersUseCase,
                                GetAllUsersQueryValidator getAllUsersQueryValidator){
    this.getAllUsersUseCase = getAllUsersUseCase;
    this.getAllUsersQueryValidator = getAllUsersQueryValidator;
  }

  @Override
  public PaginatedResult<User> handle(GetAllUsersQuery query) {
    getAllUsersQueryValidator.validate(query);
    return getAllUsersUseCase.execute(query);
  }
}
