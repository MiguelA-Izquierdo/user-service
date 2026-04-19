package com.app.userService.user.infrastructure.messaging.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


@Configuration
public class RabbitMqConfig {
  private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

  @Value("${rabbitmq.retry.maxAttempts:3}")
  private int maxAttempts;

  @Value("${rabbitmq.retry.initialInterval:1000}")
  private long initialInterval;

  @Value("${rabbitmq.retry.maxInterval:30000}")
  private long maxInterval;

  @Value("${rabbitmq.retry.multiplier:2.0}")
  private double multiplier;

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
    rabbitTemplate.setRetryTemplate(retryTemplate());
    rabbitTemplate.setMandatory(true);

    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
      if (ack) {
        logger.info("Mensaje confirmado con éxito: {}", correlationData);
      } else {
        logger.warn("Fallo en la confirmación del mensaje: {} causa: {}", correlationData, cause);
      }
    });

    return rabbitTemplate;
  }

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(initialInterval);
    backOffPolicy.setMultiplier(multiplier);
    backOffPolicy.setMaxInterval(maxInterval);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(maxAttempts);
    retryTemplate.setRetryPolicy(retryPolicy);

    return retryTemplate;
  }
}