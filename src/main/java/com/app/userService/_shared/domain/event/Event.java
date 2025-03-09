package com.app.userService._shared.domain.event;

public interface Event<T> {
  String getQueue();

  String getExchange();

  String getRoutingKey();

  String getType();
  T getPayload();
}
