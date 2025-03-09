package com.app.userService.user.application.bus.command;

import com.app.userService._shared.application.bus.command.Command;
import com.app.userService._shared.application.bus.command.CommandBus;

import java.util.HashMap;
import java.util.Map;

public record CreateUserCommand(String id, String name, String lastName, String email, String password, String countryCode,
                                String number, String documentType, String documentNumber, String street,
                                String streetNumber, String city, String state, String postalCode,
                                String country) implements Command {

  public void dispatch(CommandBus commandBus) {
  }

  public Map<String, String> getUserIdMap() {
    Map<String, String> userIdMap = new HashMap<>();

    if (id != null) {
      userIdMap.put("userId", id);
    }
    return userIdMap;
  }
  public Map<String, String> getUserNameMap() {
    Map<String, String> userNameMap = new HashMap<>();

    if (name != null) {
      userNameMap.put("userName", name);
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
  public Map<String, String> getEmailMap() {
    Map<String, String> emailMap = new HashMap<>();

    if (email != null) {
      emailMap.put("email", email);
    }
    return emailMap;
  }
  public Map<String, String> getPhoneMap() {
    Map<String, String> phoneMap = new HashMap<>();

    if (number != null && countryCode != null) {
      phoneMap.put("countryCode", countryCode);
      phoneMap.put("number", number);
    }

    return phoneMap;
  }
  public Map<String, String> getIdentityDocumentMap() {
    Map<String, String> identityDocumentMap = new HashMap<>();
    if(documentType != null && documentNumber != null){
      identityDocumentMap.put("documentType", documentType);
      identityDocumentMap.put("documentNumber", documentNumber);
    }


    return identityDocumentMap;
  }
  public Map<String, String> getAddressMap(){
    Map<String, String> addressMap = new HashMap<>();

      addressMap.put("street", street);
      addressMap.put("streetNumber", streetNumber);
      addressMap.put("city", city);
      addressMap.put("state", state);
      addressMap.put("postalCode", postalCode);
      addressMap.put("country", country);

    return addressMap;
  }
  public Map<String, String> getPasswordMap() {
    Map<String, String> passwordMap = new HashMap<>();

    passwordMap.put("password", password);

    return passwordMap;
  }
}
