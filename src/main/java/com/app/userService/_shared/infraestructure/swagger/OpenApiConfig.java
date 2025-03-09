package com.app.userService._shared.infraestructure.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("User Service API")
        .version("1.0.0")
        .description("API documentation for the User Service")
        .contact(new Contact()
          .name("Support Team")
          .email("izquierdomigueladev@gmail.com")
          )
      );
  }

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder()
      .group("user-api")
      .packagesToScan("com.app.userService.user.infrastructure.api.controller")
      .build();
  }

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
      .group("auth-api")
      .packagesToScan("com.app.userService.auth.infrastructure.api.controller")
      .build();
  }
}
