package com.app.userService.user.application.bus.command;

import com.app.userService._shared.bus.command.Command;
import com.app.userService._shared.bus.command.CommandBus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateUserCommand(
  String userId,
  String userName,
  String lastName,
  IdentityDocument identityDocument,
  Phone phone,
  Address address
) implements Command {

  public void dispatch(CommandBus commandBus) {}

  public record Phone(
    String countryCode,
    String phoneNumber
  ) {

    @JsonCreator
    public Phone(
      @JsonProperty("countryCode") String countryCode,
      @JsonProperty("phoneNumber") String phoneNumber
    ) {
      this.countryCode = countryCode != null ? countryCode : "";
      this.phoneNumber = phoneNumber != null ? phoneNumber : "";
    }

    @JsonCreator
    public static Phone fromJson(JsonNode node) {
      if (node.isObject()) {
        String countryCode = node.has("countryCode") ? node.get("countryCode").asText() : "";
        String phoneNumber = node.has("phoneNumber") ? node.get("phoneNumber").asText() : "";
        return new Phone(countryCode, phoneNumber);
      } else if (node.isTextual() || node.isNumber()) {
        return new Phone("", node.asText());
      } else {
        return new Phone("", "");
      }
    }
  }

  public record IdentityDocument(
    String documentType,
    String documentNumber
  ) {
    @JsonCreator
    public IdentityDocument(
      @JsonProperty("documentType") String documentType,
      @JsonProperty("documentNumber") String documentNumber
    ) {
      this.documentType = documentType != null ? documentType : "";
      this.documentNumber = documentNumber != null ? documentNumber : "";
    }

    @JsonCreator
    public static IdentityDocument fromJson(JsonNode node) {
      if (node.isObject()) {
        String documentType = node.has("documentType") ? node.get("documentType").asText() : "";
        String documentNumber = node.has("documentNumber") ? node.get("documentNumber").asText() : "";
        return new IdentityDocument(documentType, documentNumber);
      } else if (node.isTextual() || node.isNumber()) {
        return new IdentityDocument("", node.asText());
      } else {
        return new IdentityDocument("", "");
      }
    }
  }
  public record Address(
    String street,
    String streetNumber,
    String city,
    String state,
    String postalCode,
    String country
  ) {
    @JsonCreator
    public Address(
      @JsonProperty("street") String street,
      @JsonProperty("streetNumber") String streetNumber,
      @JsonProperty("city") String city,
      @JsonProperty("state") String state,
      @JsonProperty("postalCode") String postalCode,
      @JsonProperty("country") String country
    ) {
      this.street = street != null ? street : "";
      this.streetNumber = streetNumber != null ? streetNumber : "";
      this.city = city != null ? city : "";
      this.state = state != null ? state : "";
      this.postalCode = postalCode != null ? postalCode : "";
      this.country = country != null ? country : "";
    }

    @JsonCreator
    public static Address fromJson(JsonNode node) {
      if (node == null || node.isNull()) {
        return new Address("", "", "", "", "", "");
      }

      if (node.isObject()) {
        return new Address(
          node.has("street") ? node.get("street").asText() : "",
          node.has("streetNumber") ? node.get("streetNumber").asText() : "",
          node.has("city") ? node.get("city").asText() : "",
          node.has("state") ? node.get("state").asText() : "",
          node.has("postalCode") ? node.get("postalCode").asText() : "",
          node.has("country") ? node.get("country").asText() : ""
        );
      } else if (node.isTextual() || node.isNumber()) {
        return new Address(
          node.asText(),
          "",
          "", "", "", ""
        );
      } else {
        return new Address("", "", "", "", "", "");
      }
    }
  }

  public Map<String, String> getUserIdMap() {
    Map<String, String> userIdMap = new HashMap<>();

    if (userId != null) {
      userIdMap.put("userId", userId);
    }
    return userIdMap;
  }
  public Map<String, String> getUserNameMap() {
    Map<String, String> userNameMap = new HashMap<>();

    if (userName != null) {
      userNameMap.put("userName", userName);
    }
    return userNameMap;
  }
  public Map<String, String> getLastNameMap() {
    Map<String, String> userNameMap = new HashMap<>();

    if (lastName != null) {
      userNameMap.put("lastName", lastName);
    }
    return userNameMap;
  }
  public Map<String, String> getIdentityDocumentMap() {
    Map<String, String> identityDocumentMap = new HashMap<>();

    if (identityDocument != null) {
      identityDocumentMap.put("documentType", identityDocument.documentType);
      identityDocumentMap.put("documentNumber", identityDocument.documentNumber);
    }
    return identityDocumentMap;
  }
  public Map<String, String> getPhoneMap() {
    Map<String, String> phoneMap = new HashMap<>();

    if (phone != null) {
      phoneMap.put("countryCode", phone.countryCode);
      phoneMap.put("number", phone.phoneNumber);
    }

    return phoneMap;
  }
  public Map<String, String> getAddressMap(){
    Map<String, String> addressMap = new HashMap<>();

    if(address != null){
      addressMap.put("street", address.street);
      addressMap.put("streetNumber", address.streetNumber);
      addressMap.put("city", address.city);
      addressMap.put("state", address.state);
      addressMap.put("postalCode", address.postalCode);
      addressMap.put("country", address.country);
    }
    return addressMap;
  }
}

