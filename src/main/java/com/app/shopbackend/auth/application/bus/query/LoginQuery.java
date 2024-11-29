package com.app.shopbackend.auth.application.bus.query;

import com.app.shopbackend._shared.bus.query.Query;
import com.app.shopbackend._shared.bus.query.QueryBus;
import com.app.shopbackend.auth.application.dto.UserLoggedDTO;

public record LoginQuery(String email, String password) implements Query<UserLoggedDTO> {
  public void dispatch(QueryBus queryBus) {
  }
}
