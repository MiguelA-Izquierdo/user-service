package com.app.userService._shared.bus.query;


public interface QueryHandler<Q extends Query<R>, R> {
  R handle(Q query) ;
}
