package com.app.userService.user.domain.event;


import java.util.UUID;

public interface UserEvent {
  UUID getUserId();
  String getRoutingKey();
}
