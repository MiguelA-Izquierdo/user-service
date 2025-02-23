package com.app.userService.user.application.service;

import com.app.userService.auth.application.service.PasswordRestTokenService;
import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.auth.domain.service.TokenService;
import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import com.app.userService.user.domain.event.UserLockedDomainEvent;
import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

  public void handleUserDeletedEvent(User user) {
    UserDeletedDomainEvent event = userEventFactory.createUserDeletedEvent(user);

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
  public void handleUserLockedEvent(User user) {
    final String token = tokenService.generateToken();
    final LocalDateTime expirationTime = LocalDateTime.now()
      .plusSeconds(PasswordResetToken.TOKEN_EXPIRATION_TIME_SECONDS);

    PasswordResetToken passwordResetToken = PasswordResetToken.of(
      UUID.randomUUID(),
      user,
      token,
      expirationTime,
      false);
    passwordRestTokenService.createPasswordRestToken(passwordResetToken);

    UserLockedDomainEvent event = userEventFactory.createUserLockedEvent(user, token);

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
