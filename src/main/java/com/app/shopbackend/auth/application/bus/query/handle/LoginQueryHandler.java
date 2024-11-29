package com.app.shopbackend.auth.application.bus.query.handle;

import com.app.shopbackend._shared.bus.query.QueryHandler;

import com.app.shopbackend.auth.application.bus.query.LoginQuery;
import com.app.shopbackend.auth.application.dto.UserLoggedDTO;
import com.app.shopbackend.auth.application.useCase.LoginUseCase;
import com.app.shopbackend.auth.application.validation.LoginQueryValidator;


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
