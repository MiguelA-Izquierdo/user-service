package com.app.userService._shared.application.bus.query;


public interface QueryHandler<Q extends Query<R>, R> {
  R handle(Q query) ;
}
