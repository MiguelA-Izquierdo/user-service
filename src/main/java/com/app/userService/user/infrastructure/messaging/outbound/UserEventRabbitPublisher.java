package com.app.userService.user.infrastructure.messaging.outbound;

import com.app.userService._shared.domain.event.Event;
import com.app.userService.user.domain.service.EventPublisher;
import com.app.userService._shared.infraestructure.serialization.JsonSerializationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@Service
public class UserEventRabbitPublisher implements EventPublisher {
  private static final Logger logger = LoggerFactory.getLogger(UserEventRabbitPublisher.class);

  private final RabbitTemplate rabbitTemplate;
  private final JsonSerializationService jsonSerializationService;

  @Value("${messaging.exchange.user}")
  private String userExchange;

  public UserEventRabbitPublisher(RabbitTemplate rabbitTemplate, JsonSerializationService jsonSerializationService) {
    this.rabbitTemplate = rabbitTemplate;
    this.jsonSerializationService = jsonSerializationService;
  }


  @Override
  public <T> void publish(Event<T> event) {
    try {
      String jsonEvent = jsonSerializationService.serialize(event);

      CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

      rabbitTemplate.convertAndSend(userExchange, event.getRoutingKey(), jsonEvent, correlationData);

    } catch (JsonProcessingException e) {
      logger.error("Error al serializar el evento: {}", e.getMessage());
      throw new RuntimeException("No se pudo serializar el evento", e);
    }
  }


}

