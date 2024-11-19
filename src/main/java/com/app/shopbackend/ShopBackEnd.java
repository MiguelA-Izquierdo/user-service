package com.app.shopbackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Objects;

@SpringBootApplication
@EnableAsync
public class ShopBackEnd {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().load();

    System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
    System.setProperty("DB_USER", Objects.requireNonNull(dotenv.get("DB_USER")));
    System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));

    SpringApplication.run(ShopBackEnd.class, args);
  }

}
