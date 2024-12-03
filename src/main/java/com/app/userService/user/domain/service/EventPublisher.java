package com.app.userService.user.domain.service;

import com.app.userService.user.domain.event.UserEvent;

public interface EventPublisher {
  void publish(UserEvent event);
}
