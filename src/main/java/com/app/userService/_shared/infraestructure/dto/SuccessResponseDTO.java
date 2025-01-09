package com.app.userService._shared.infraestructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

public class SuccessResponseDTO {

  @Schema(description = "Indicates whether the user creation was successful", example = "true")
  private final boolean success;

  @Schema(description = "HTTP status code of the response", example = "201")
  private final int status;

  @Schema(description = "Message providing more details about the response", example = "User created successfully")
  private final String message;

  @Schema(description = "Optional data related to the response, may be null if no additional data is provided", example = "{\"userId\": 123}")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Object data;

  private SuccessResponseDTO(int status, String message, Object data) {
    this.success = true;
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public static SuccessResponseDTO Of(int status, String message) {
    return new SuccessResponseDTO( status, message, null);
  }

  public static SuccessResponseDTO Of(int status, String message, Object data) {
    return new SuccessResponseDTO(status, message, data);
  }

  public boolean success() {
    return success;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public Object getData() {
    return data;
  }
}
