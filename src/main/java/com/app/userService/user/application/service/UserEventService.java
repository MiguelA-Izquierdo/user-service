package com.app.userService.user.application.service;

import com.app.userService.user.domain.event.UserCreatedEvent;
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

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;

  public UserEventService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
    this.outboxEventRepository = outboxEventRepository;
    this.objectMapper = objectMapper;
  }
  public void handleUserCreatedEvent(User user) {
    UserCreatedEvent event = UserCreatedEvent.of(
      user.getId().getValue(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      user.getEmail().getEmail());

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
