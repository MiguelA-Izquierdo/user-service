package com.app.userService.auth.application.bus.query;

import com.app.userService._shared.application.bus.query.Query;
import com.app.userService._shared.application.bus.query.QueryBus;
import com.app.userService.auth.application.dto.UserLoggedDTO;

public record LoginQuery(String email, String password) implements Query<UserLoggedDTO> {
  public void dispatch(QueryBus queryBus) {
  }
}
