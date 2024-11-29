package com.app.shopbackend._shared.bus.query;

public interface QueryBus {
  <R, Q extends Query<R>> R send(Q query);
}
