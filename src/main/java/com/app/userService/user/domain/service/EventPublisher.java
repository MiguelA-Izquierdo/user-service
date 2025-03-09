package com.app.userService.user.domain.service;

import com.app.userService._shared.domain.event.Event;

public interface EventPublisher {
  <T> void publish(Event<T> event);
}
