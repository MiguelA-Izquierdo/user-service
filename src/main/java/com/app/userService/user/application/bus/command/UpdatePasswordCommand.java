package com.app.userService.user.application.bus.command;

import com.app.userService._shared.bus.command.Command;
import com.app.userService._shared.bus.command.CommandBus;

import java.util.HashMap;
import java.util.Map;

public record UpdatePasswordCommand(String id, String currentPassword, String newPassword) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
  public Map<String, String> getUserIdMap() {
    Map<String, String> userIdMap = new HashMap<>();

    if (id != null) {
      userIdMap.put("userId", id);
    }
    return userIdMap;
  }

  public Map<String, String> getCurrentPasswordMap() {
    Map<String, String> passwordMap = new HashMap<>();
    if(currentPassword != null){
      passwordMap.put("password", currentPassword);
    }

    return passwordMap;
  }

  public Map<String, String> getNewPasswordMap() {
    Map<String, String> passwordMap = new HashMap<>();

    if(newPassword != null){
      passwordMap.put("password", newPassword);
    }

    return passwordMap;
  }
}
