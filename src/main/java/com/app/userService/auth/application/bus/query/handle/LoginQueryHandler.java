package com.app.userService.auth.application.bus.query.handle;

import com.app.userService._shared.application.bus.query.QueryHandler;

import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import com.app.userService.auth.application.useCases.LoginUseCase;
import com.app.userService.auth.application.validation.LoginQueryValidator;


import org.springframework.stereotype.Service;



@Service
public class LoginQueryHandler implements QueryHandler<LoginQuery,UserLoggedDTO> {
  private final LoginUseCase loginUseCase;
  private final LoginQueryValidator loginQueryValidator;

  public LoginQueryHandler(LoginUseCase loginUseCase, LoginQueryValidator loginQueryValidator) {
    this.loginUseCase = loginUseCase;
    this.loginQueryValidator = loginQueryValidator;
  }

  @Override
  public UserLoggedDTO handle(LoginQuery query) {
    this.loginQueryValidator.validate(query);
    return this.loginUseCase.execute(query);
  }


}
