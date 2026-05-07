package com.app.userService._shared.domain.event;

import java.util.UUID;

public interface Event<T> {
  UUID getEventId();

  String getQueue();

  String getExchange();

  String getRoutingKey();

  String getType();

  T getPayload();
}
