package com.app.shopbackend._shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
    // Crear el mapa con la respuesta personalizada
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", HttpStatus.FORBIDDEN.value()); // 403
    errorResponse.put("message", "Access Denied: You do not have permission to access this resource.");

    // Establecer el código de estado de la respuesta
    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Código 403 Forbidden
    response.setContentType("application/json");

    // Convertir el mapa en JSON
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResponse = objectMapper.writeValueAsString(errorResponse);

    // Escribir la respuesta JSON al cuerpo de la respuesta
    response.getWriter().write(jsonResponse);
  }
}
