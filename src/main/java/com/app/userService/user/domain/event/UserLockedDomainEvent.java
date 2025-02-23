package com.app.userService.user.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserLockedDomainEvent implements Event<UserLockedDomainEvent.UserPayload> {

  private static final Logger logger = LoggerFactory.getLogger(UserLockedDomainEvent.class);

  private final String userExchange;
  private final String userLockedQueue;
  private final String userLockedRoutingKey;
  private final UserPayload payload;

  public UserLockedDomainEvent(String userExchange,
                               String userLockedQueue,
                               String userLockedRoutingKey,
                               UUID userId,
                               String name,
                               String lastName,
                               String email,
                               String token,
                               LocalDateTime expirationDate) {
    this.userExchange = userExchange;
    this.userLockedQueue = userLockedQueue;
    this.userLockedRoutingKey = userLockedRoutingKey;
    this.payload = new UserPayload(userId, name, lastName, email, token, expirationDate);
  }

  @Override
  public String getQueue() {
    return userLockedQueue;
  }

  @Override
  public String getExchange() {
    return userExchange;
  }

  @Override
  public String getRoutingKey() {
    return userLockedRoutingKey;
  }

  @Override
  public String getType() {
    return "user.locked";
  }

  @Override
  public UserPayload getPayload() {
    return this.payload;
  }

  public record UserPayload(UUID userId, String name, String lastName, String email, String token, LocalDateTime expirationDate) {}
}
