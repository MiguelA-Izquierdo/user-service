package com.app.userService;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class UserService {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().load();

    System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
    System.setProperty("DB_USER", Objects.requireNonNull(dotenv.get("DB_USER")));
    System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
    System.setProperty("JWT_SECRET", Objects.requireNonNull(dotenv.get("JWT_SECRET")));
    System.setProperty("RABBITMQ_USER_SERVICE_HOST", Objects.requireNonNull(dotenv.get("RABBITMQ_USER_SERVICE_HOST")));
    System.setProperty("RABBITMQ_USER_SERVICE_PORT", Objects.requireNonNull(dotenv.get("RABBITMQ_USER_SERVICE_PORT")));
    System.setProperty("RABBITMQ_USER_SERVICE_USER_NAME", Objects.requireNonNull(dotenv.get("RABBITMQ_USER_SERVICE_USER_NAME")));
    System.setProperty("RABBITMQ_USER_SERVICE_PASSWORD", Objects.requireNonNull(dotenv.get("RABBITMQ_USER_SERVICE_PASSWORD")));

    SpringApplication.run(UserService.class, args);
  }

}
