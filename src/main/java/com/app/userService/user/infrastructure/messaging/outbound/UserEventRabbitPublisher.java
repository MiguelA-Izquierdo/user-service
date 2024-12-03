package com.app.userService.user.infrastructure.messaging.outbound;

import com.app.userService.user.domain.event.UserEvent;
import com.app.userService.user.domain.service.EventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

@Service
public class UserEventRabbitPublisher implements EventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Value("${messaging.exchange.user}")
  private String userExchange;

  public UserEventRabbitPublisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }


  @Override
  public void publish(UserEvent event) {
    String routingKey = event.getRoutingKey();

    rabbitTemplate.convertAndSend(userExchange, routingKey, event);
  }

}

