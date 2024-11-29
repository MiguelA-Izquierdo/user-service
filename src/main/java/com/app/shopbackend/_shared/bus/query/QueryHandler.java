package com.app.shopbackend._shared.bus.query;

public interface QueryHandler<Q extends Query<R>, R> {
  R handle(Q query);
}
