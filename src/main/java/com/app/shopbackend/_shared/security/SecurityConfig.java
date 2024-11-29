package com.app.shopbackend._shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final SecurityPropertiesService securityPropertiesService;


  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter
    ,SecurityPropertiesService securityPropertiesService) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.securityPropertiesService = securityPropertiesService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    Map<HttpMethod, String[]> publicRoutes = getPublicRoutes();

    http
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> {
        publicRoutes.forEach((method, routes) ->
          Arrays.stream(routes)
            .forEach(route -> auth.requestMatchers(method, route).permitAll())
        );
        auth.anyRequest().authenticated();
      })
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .exceptionHandling(exceptionHandlingCustomizer())
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  private Map<HttpMethod, String[]> getPublicRoutes() {
    Map<HttpMethod, String[]> publicRoutes = new HashMap<>();
    publicRoutes.put(HttpMethod.POST, securityPropertiesService.getPublicPostRoutes());
    publicRoutes.put(HttpMethod.GET, securityPropertiesService.getPublicGetRoutes());
    publicRoutes.put(HttpMethod.PUT, securityPropertiesService.getPublicPutRoutes());
    publicRoutes.put(HttpMethod.PATCH, securityPropertiesService.getPublicPatchRoutes());
    return publicRoutes;
  }
  private Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandlingCustomizer() {
    return (exceptionHandling) -> exceptionHandling
      .accessDeniedHandler((request, response, accessDeniedException) -> {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"error\",\"code\":403,\"message\":\"Access Denied: You do not have permission to access this resource.\"}");
      });
  }
}
