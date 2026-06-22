package com.app.userService.user.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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

  @Value("${messaging.exchange.dlx}")
  private String dlxExchange;

  @Value("${messaging.queue.userCreated}")
  private String userCreatedQueue;

  @Value("${messaging.queue.userUpdated}")
  private String userUpdatedQueue;

  @Value("${messaging.queue.userDeleted}")
  private String userDeletedQueue;

  @Value("${messaging.queue.userLocked}")
  private String userLockedQueue;

  @Value("${messaging.queue.userUnlocked}")
  private String userUnlockedQueue;

  @Value("${messaging.queue.userLogged}")
  private String userLoggedQueue;

  @Value("${messaging.queue.userCreated.dlq}")
  private String userCreatedDlq;

  @Value("${messaging.queue.userUpdated.dlq}")
  private String userUpdatedDlq;

  @Value("${messaging.queue.userDeleted.dlq}")
  private String userDeletedDlq;

  @Value("${messaging.queue.userLocked.dlq}")
  private String userLockedDlq;

  @Value("${messaging.queue.userUnlocked.dlq}")
  private String userUnlockedDlq;

  @Value("${messaging.queue.userLogged.dlq}")
  private String userLoggedDlq;

  @Value("${messaging.routing.key.userCreated}")
  private String userCreatedRoutingKey;

  @Value("${messaging.routing.key.userUpdated}")
  private String userUpdatedRoutingKey;

  @Value("${messaging.routing.key.userDeleted}")
  private String userDeletedRoutingKey;

  @Value("${messaging.routing.key.userLocked}")
  private String userLockedRoutingKey;

  @Value("${messaging.routing.key.userUnlocked}")
  private String userUnlockedRoutingKey;

  @Value("${messaging.routing.key.userLogged}")
  private String userLoggedRoutingKey;

  @Value("${messaging.routing.key.userLogged.with.token}")
  private String userLoggedWithTokenRoutingKey;

  @Value("${messaging.routing.key.userLogged.without.token}")
  private String userLoggedWithoutTokenRoutingKey;

  @Value("${messaging.routing.key.userLogged.logout}")
  private String userLogoutRoutingKey;

  // --- Main exchange ---

  @Bean
  public TopicExchange userExchange() {
    return new TopicExchange(userExchange);
  }

  // --- Dead Letter Exchange ---

  @Bean
  public DirectExchange deadLetterExchange() {
    return new DirectExchange(dlxExchange);
  }

  // --- Main queues (with DLX routing) ---

  @Bean
  public Queue userCreatedQueue() {
    return QueueBuilder.durable(userCreatedQueue)
      .withArgument("x-dead-letter-exchange", dlxExchange)
      .withArgument("x-dead-letter-routing-key", userCreatedDlq)
      .build();
  }

  @Bean
  public Queue userUpdatedQueue() {
    return QueueBuilder.durable(userUpdatedQueue)
      .withArgument("x-dead-letter-exchange", dlxExchange)
      .withArgument("x-dead-letter-routing-key", userUpdatedDlq)
      .build();
  }

  @Bean
  public Queue userDeletedQueue() {
    return QueueBuilder.durable(userDeletedQueue)
      .withArgument("x-dead-letter-exchange", dlxExchange)
      .withArgument("x-dead-letter-routing-key", userDeletedDlq)
      .build();
  }

  @Bean
  public Queue userLockedQueue() {
    return QueueBuilder.durable(userLockedQueue)
      .withArgument("x-dead-letter-exchange", dlxExchange)
      .withArgument("x-dead-letter-routing-key", userLockedDlq)
      .build();
  }

  @Bean
  public Queue userUnlockedQueue() {
    return QueueBuilder.durable(userUnlockedQueue)
      .withArgument("x-dead-letter-exchange", dlxExchange)
      .withArgument("x-dead-letter-routing-key", userUnlockedDlq)
      .build();
  }

  @Bean
  public Queue userLoggedQueue() {
    return QueueBuilder.durable(userLoggedQueue)
      .withArgument("x-dead-letter-exchange", dlxExchange)
      .withArgument("x-dead-letter-routing-key", userLoggedDlq)
      .build();
  }

  // --- Dead Letter Queues ---

  @Bean
  public Queue userCreatedDlqQueue() {
    return new Queue(userCreatedDlq, true);
  }

  @Bean
  public Queue userUpdatedDlqQueue() {
    return new Queue(userUpdatedDlq, true);
  }

  @Bean
  public Queue userDeletedDlqQueue() {
    return new Queue(userDeletedDlq, true);
  }

  @Bean
  public Queue userLockedDlqQueue() {
    return new Queue(userLockedDlq, true);
  }

  @Bean
  public Queue userUnlockedDlqQueue() {
    return new Queue(userUnlockedDlq, true);
  }

  @Bean
  public Queue userLoggedDlqQueue() {
    return new Queue(userLoggedDlq, true);
  }

  // --- Main queue bindings ---

  @Bean
  public Binding userCreatedBinding() {
    return BindingBuilder.bind(userCreatedQueue()).to(userExchange()).with(userCreatedRoutingKey);
  }

  @Bean
  public Binding userUpdatedBinding() {
    return BindingBuilder.bind(userUpdatedQueue()).to(userExchange()).with(userUpdatedRoutingKey);
  }

  @Bean
  public Binding userDeletedBinding() {
    return BindingBuilder.bind(userDeletedQueue()).to(userExchange()).with(userDeletedRoutingKey);
  }

  @Bean
  public Binding userLockedBinding() {
    return BindingBuilder.bind(userLockedQueue()).to(userExchange()).with(userLockedRoutingKey);
  }

  @Bean
  public Binding userUnlockedBinding() {
    return BindingBuilder.bind(userUnlockedQueue()).to(userExchange()).with(userUnlockedRoutingKey);
  }

  @Bean
  public Binding userLoggedWithTokenBinding() {
    return BindingBuilder.bind(userLoggedQueue()).to(userExchange()).with(userLoggedWithTokenRoutingKey);
  }

  @Bean
  public Binding userLoggedWithoutTokenBinding() {
    return BindingBuilder.bind(userLoggedQueue()).to(userExchange()).with(userLoggedWithoutTokenRoutingKey);
  }

  @Bean
  public Binding userLogoutBinding() {
    return BindingBuilder.bind(userLoggedQueue()).to(userExchange()).with(userLogoutRoutingKey);
  }

  // --- DLQ bindings ---

  @Bean
  public Binding userCreatedDlqBinding() {
    return BindingBuilder.bind(userCreatedDlqQueue()).to(deadLetterExchange()).with(userCreatedDlq);
  }

  @Bean
  public Binding userUpdatedDlqBinding() {
    return BindingBuilder.bind(userUpdatedDlqQueue()).to(deadLetterExchange()).with(userUpdatedDlq);
  }

  @Bean
  public Binding userDeletedDlqBinding() {
    return BindingBuilder.bind(userDeletedDlqQueue()).to(deadLetterExchange()).with(userDeletedDlq);
  }

  @Bean
  public Binding userLockedDlqBinding() {
    return BindingBuilder.bind(userLockedDlqQueue()).to(deadLetterExchange()).with(userLockedDlq);
  }

  @Bean
  public Binding userUnlockedDlqBinding() {
    return BindingBuilder.bind(userUnlockedDlqQueue()).to(deadLetterExchange()).with(userUnlockedDlq);
  }

  @Bean
  public Binding userLoggedDlqBinding() {
    return BindingBuilder.bind(userLoggedDlqQueue()).to(deadLetterExchange()).with(userLoggedDlq);
  }
}