package com.app.userService.user.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateUserCommandDTO(
  @JsonProperty("id")
  @Schema(description = "Unique identifier for the user (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
  String id,

  @JsonProperty("name")
  @Schema(description = "The user's first name", example = "John")
  String name,

  @JsonProperty("lastName")
  @Schema(description = "The user's last name", example = "Doe")
  String lastName,

  @JsonProperty("email")
  @Schema(description = "The user's email address", example = "john.doe@example.com")
  String email,

  @JsonProperty("password")
  @Schema(description = "The user's password", example = "P@ssw0rd123")
  String password,

  @JsonProperty("countryCode")
  @Schema(description = "The user's country code", example = "US")
  String countryCode,

  @JsonProperty("number")
  @Schema(description = "The user's phone number", example = "1234567890")
  String number,

  @JsonProperty("documentType")
  @Schema(description = "The user's document type", example = "Passport")
  String documentType,

  @JsonProperty("documentNumber")
  @Schema(description = "The user's document number", example = "123456789")
  String documentNumber,

  @JsonProperty("street")
  @Schema(description = "The user's street address", example = "123 Main St")
  String street,

  @JsonProperty("streetNumber")
  @Schema(description = "The street number of the address", example = "12")
  String streetNumber,

  @JsonProperty("city")
  @Schema(description = "The user's city", example = "New York")
  String city,

  @JsonProperty("state")
  @Schema(description = "The user's state", example = "NY")
  String state,

  @JsonProperty("postalCode")
  @Schema(description = "The postal code of the user's address", example = "10001")
  String postalCode,

  @JsonProperty("country")
  @Schema(description = "The country where the user resides", example = "USA")
  String country
) {}

