package com.app.userService.user.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class UserRabbitMqConfig {

  @Value("${messaging.exchange.user}")
  private String userExchange;

  @Value("${messaging.queue.userCreated}")
  private String userCreatedQueue;

  @Value("${messaging.queue.userUpdated}")
  private String userUpdatedQueue;

  @Value("${messaging.queue.userDeleted}")
  private String userDeletedQueue;
  @Value("${messaging.queue.userLocked}")
  private String userLockedQueue;
  @Value("${messaging.queue.userLogged}")
  private String userLoggedQueue;

  @Value("${messaging.routing.key.userCreated}")
  private String userCreatedRoutingKey;

  @Value("${messaging.routing.key.userUpdated}")
  private String userUpdatedRoutingKey;

  @Value("${messaging.routing.key.userDeleted}")
  private String userDeletedRoutingKey;
  @Value("${messaging.routing.key.userLocked}")
  private String userLockedRoutingKey;
  @Value("${messaging.routing.key.userLogged}")
  private String userLoggedRoutingKey;
  @Value("${messaging.routing.key.userLogged.with.token}")
  private String userLoggedWithTokenRoutingKey;
  @Value("${messaging.routing.key.userLogged.without.token}")
  private String userLoggedWithoutTokenRoutingKey;
  @Value("${messaging.routing.key.userLogged.logout}")
  private String userLogoutRoutingKey;


  @Bean
  public Queue userCreatedQueue() {
    return new Queue(userCreatedQueue, true);
  }

  @Bean
  public Queue userUpdatedQueue() {
    return new Queue(userUpdatedQueue, true);
  }

  @Bean
  public Queue userDeletedQueue() {
    return new Queue(userDeletedQueue, true);
  }
  @Bean
  public Queue userLockedQueue() {
    return new Queue(userLockedQueue, true);
  }
  @Bean
  public Queue userLoggedQueue() {
    return new Queue(userLoggedQueue, true);
  }

  @Bean
  public TopicExchange userExchange() {
    return new TopicExchange(userExchange);
  }

  @Bean
  public Binding userCreatedBinding() {
    return new Binding(userCreatedQueue, Binding.DestinationType.QUEUE,
      userExchange, userCreatedRoutingKey, null);
  }

  @Bean
  public Binding userUpdatedBinding() {
    return new Binding(userUpdatedQueue, Binding.DestinationType.QUEUE,
      userExchange, userUpdatedRoutingKey, null);
  }

  @Bean
  public Binding userDeletedBinding() {
    return new Binding(userDeletedQueue, Binding.DestinationType.QUEUE,
      userExchange, userDeletedRoutingKey, null);
  }
  @Bean
  public Binding userLockeddBinding() {
    return new Binding(userLockedQueue, Binding.DestinationType.QUEUE,
      userExchange, userLockedRoutingKey, null);
  }
  @Bean
  public Binding userLoggedWithTokenBinding() {
    return new Binding(userLoggedQueue, Binding.DestinationType.QUEUE,
      userExchange, userLoggedWithTokenRoutingKey, null);
  }
  @Bean
  public Binding userLoggedWithoutTokenBinding() {
    return new Binding(userLoggedQueue, Binding.DestinationType.QUEUE,
      userExchange, userLoggedWithoutTokenRoutingKey, null);
  }
  @Bean
  public Binding userLogoutBinding() {
    return new Binding(userLoggedQueue, Binding.DestinationType.QUEUE,
      userExchange, userLogoutRoutingKey, null);
  }
}
