package com.app.userService._shared.application.bus.query;

public interface QueryBus {
  <R, Q extends Query<R>> R send(Q query);
}
