package com.app.userService._shared.application.service;

import com.app.userService._shared.domain.event.Event;

import com.app.userService.auth.application.service.PasswordRestTokenService;
import com.app.userService.auth.domain.event.UserLoggedDomainEvent;
import com.app.userService.auth.domain.event.UserLogoutDomainEvent;
import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.auth.domain.service.TokenService;
import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import com.app.userService.auth.domain.event.UserLockedDomainEvent;
import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserEventService {
  private final TokenService tokenService;
  private final PasswordRestTokenService passwordRestTokenService;
  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;
  private final UserEventFactory userEventFactory;

  public UserEventService(OutboxEventRepository outboxEventRepository,
                          ObjectMapper objectMapper,
                          TokenService tokenService,
                          PasswordRestTokenService passwordRestTokenService,
                          UserEventFactory userEventFactory) {
    this.outboxEventRepository = outboxEventRepository;
    this.objectMapper = objectMapper;
    this.tokenService = tokenService;
    this.passwordRestTokenService = passwordRestTokenService;
    this.userEventFactory = userEventFactory;
  }

  public void handleUserCreatedEvent(User user) {
    UserCreatedDomainEvent event = userEventFactory.createUserCreatedEvent(user);
    storeOutboxEvent(event);
  }

  public void handleUserDeletedEvent(User user) {
    UserDeletedDomainEvent event = userEventFactory.createUserDeletedEvent(user);
    storeOutboxEvent(event);
  }

  public void handleUserLockedEvent(User user) {
    final String token = tokenService.generateToken();
    final LocalDateTime expirationTime = LocalDateTime.now()
      .plusSeconds(PasswordResetToken.TOKEN_EXPIRATION_TIME_SECONDS);
    PasswordResetToken passwordResetToken = PasswordResetToken.of(
      UUID.randomUUID(),
      user,
      token,
      expirationTime,
      false
    );
    passwordRestTokenService.createPasswordRestToken(passwordResetToken);
    UserLockedDomainEvent event = userEventFactory.createUserLockedEvent(user, token);
    storeOutboxEvent(event);
  }

  public void handleUserLoggedEvent(User user, boolean isUsingToken) {
    UserLoggedDomainEvent event = userEventFactory.createUserLoggedEvent(user, isUsingToken);
    storeOutboxEvent(event);
  }
  public void handleUserLogoutEvent(User user) {
    UserLogoutDomainEvent event = userEventFactory.createUserLogoutEvent(user);
    storeOutboxEvent(event);
  }

  private void storeOutboxEvent(Event<?> event) {
    try {
      String payload = objectMapper.writeValueAsString(event.getPayload());
      OutboxEvent outboxEvent = OutboxEvent.of(
        UUID.randomUUID(),
        event.getType(),
        payload,
        OutboxEventStatus.PENDING,
        LocalDateTime.now(),
        event.getQueue(),
        event.getRoutingKey(),
        event.getExchange()
      );
      outboxEventRepository.save(outboxEvent);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error serializing event payload", e);
    }
  }
}
