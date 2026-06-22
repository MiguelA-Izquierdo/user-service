package com.app.userService._shared.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final SecurityPropertiesService securityPropertiesService;

  @Value("${security.hsts.enabled:false}")
  private boolean hstsEnabled;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        SecurityPropertiesService securityPropertiesService) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.securityPropertiesService = securityPropertiesService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .headers(headers -> {
        headers.frameOptions(f -> f.deny());
        headers.contentTypeOptions(Customizer.withDefaults());
        headers.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"));
        headers.referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
        if (hstsEnabled) {
          headers.httpStrictTransportSecurity(hsts -> hsts
            .maxAgeInSeconds(31536000)
            .includeSubDomains(true)
          );
        } else {
          headers.httpStrictTransportSecurity(hsts -> hsts.disable());
        }
      })
      .authorizeHttpRequests(auth -> auth
        // Health endpoint (incl. liveness/readiness probes) must be reachable without auth.
        // The security filter chain is also active on the separate management port, so k8s
        // probes would otherwise get 401. Detailed health output stays gated by show-details.
        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
        .requestMatchers(securityPropertiesService::isPublicRoute).permitAll()
        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
          .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
        .anyRequest().authenticated()
      )
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

  // The JWT filter is a @Component, so Spring Boot would auto-register it as a plain servlet
  // filter on EVERY servlet context — including the separate Actuator management port, where it
  // would 401 the unauthenticated k8s probes. Disabling the auto-registration keeps the filter
  // active only via the SecurityFilterChain below (application port), and also stops it running
  // twice on the application port.
  @Bean
  public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
    FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
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
