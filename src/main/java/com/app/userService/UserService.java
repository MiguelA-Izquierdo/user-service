package com.app.userService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class UserService {
  public static void main(String[] args) {
    SpringApplication.run(UserService.class, args);
  }

}
