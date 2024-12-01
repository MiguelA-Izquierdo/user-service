package com.app.shopbackend.user.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

  @Value("${messaging.queue.user}")
  private String userQueue;
  @Value("${messaging.exchange.user}")
  private String userExchange;
  @Value("${messaging.routing.key.user}")
  private String userRoutingKey;

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
    return rabbitTemplate;
  }
  @Bean
  public Queue userQueue() {
    return new Queue(userQueue, true);
  }

  @Bean
  public TopicExchange userExchange() {
    return new TopicExchange(userExchange);
  }

  @Bean
  public Binding userBinding(Queue userQueue, TopicExchange userExchange) {
    return new Binding(userQueue.getName(), Binding.DestinationType.QUEUE, userExchange.getName(), userRoutingKey, null);
  }
}
