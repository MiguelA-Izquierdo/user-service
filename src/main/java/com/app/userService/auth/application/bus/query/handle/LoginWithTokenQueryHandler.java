package com.app.userService.auth.application.bus.query.handle;

import com.app.userService._shared.bus.query.QueryHandler;
import com.app.userService.auth.application.bus.query.LoginWithTokenQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import com.app.userService.auth.application.useCases.LoginWithTokenUseCase;
import org.springframework.stereotype.Service;


@Service
public class LoginWithTokenQueryHandler implements QueryHandler<LoginWithTokenQuery,UserLoggedDTO> {
  private final LoginWithTokenUseCase loginWithTokenUseCase;

  public LoginWithTokenQueryHandler(LoginWithTokenUseCase loginWithTokenUseCase) {
    this.loginWithTokenUseCase = loginWithTokenUseCase;
  }

  @Override
  public UserLoggedDTO handle(LoginWithTokenQuery query) {

    return this.loginWithTokenUseCase.execute(query);
  }


}
