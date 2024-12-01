package com.app.shopbackend.user.domain.service;

import com.app.shopbackend.user.domain.event.UserEvent;

public interface EventPublisher {
  void publish(UserEvent event);
}
