package com.app.userService.user.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonSerializationService {

  private final ObjectMapper objectMapper;

  public JsonSerializationService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String serialize(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
