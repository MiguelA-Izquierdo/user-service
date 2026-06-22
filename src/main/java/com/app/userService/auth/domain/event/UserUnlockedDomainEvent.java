package com.app.userService.auth.domain.event;

import com.app.userService._shared.domain.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UserUnlockedDomainEvent implements Event<UserUnlockedDomainEvent.UserPayload> {

  private static final Logger logger = LoggerFactory.getLogger(UserUnlockedDomainEvent.class);

  private final UUID eventId;
  private final String userExchange;
  private final String userUnlockedQueue;
  private final String userUnlockedRoutingKey;
  private final UserPayload payload;

  public UserUnlockedDomainEvent(String userExchange,
                                 String userUnlockedQueue,
                                 String userUnlockedRoutingKey,
                                 UUID userId,
                                 String name,
                                 String lastName,
                                 String email) {
    this.eventId = UUID.randomUUID();
    this.userExchange = userExchange;
    this.userUnlockedQueue = userUnlockedQueue;
    this.userUnlockedRoutingKey = userUnlockedRoutingKey;
    this.payload = new UserPayload(userId, name, lastName, email);
  }

  @Override
  public UUID getEventId() {
    return eventId;
  }

  @Override
  public String getQueue() {
    return userUnlockedQueue;
  }

  @Override
  public String getExchange() {
    return userExchange;
  }

  @Override
  public String getRoutingKey() {
    return userUnlockedRoutingKey;
  }

  @Override
  public String getType() {
    return "user.unlocked";
  }

  @Override
  public UserPayload getPayload() {
    return this.payload;
  }

  public record UserPayload(UUID userId, String name, String lastName, String email) {}
}
