package com.app.userService._shared.infraestructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

public class ErrorResponseDTO {

  @Schema(description = "Indicates whether the response was successful", example = "false")
  private final boolean success;

  @Schema(description = "HTTP status code of the response", example = "400")
  private final int status;

  @Schema(description = "Message providing more details about the response", example = "Invalid request data")
  private final String message;

  @Schema(description = "Optional list of errors providing more details about the failure", example = "[\"Invalid email format\", \"Password is too short\"]")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Map<String, String> errors;

  private ErrorResponseDTO(int status, String message, Map<String, String>  errors) {
    this.success = false;
    this.status = status;
    this.message = message;
    this.errors = errors;
  }

  public static ErrorResponseDTO Of(int status, String message) {
    return new ErrorResponseDTO(status, message, null);
  }

  public static ErrorResponseDTO Of(int status, String message, Map<String, String>  errors) {
    return new ErrorResponseDTO(status, message, errors);
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

  public Map<String, String>  getErrors() {
    return errors;
  }
}
