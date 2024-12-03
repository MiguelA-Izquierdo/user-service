package com.app.userService.user.application.bus.command;

import com.app.userService._shared.bus.command.Command;
import com.app.userService._shared.bus.command.CommandBus;

public record UpdatePasswordCommand(String id, String currentPassword, String newPassword) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
}
