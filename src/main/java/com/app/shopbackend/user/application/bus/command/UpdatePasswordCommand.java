package com.app.shopbackend.user.application.bus.command;

import com.app.shopbackend._shared.bus.command.Command;
import com.app.shopbackend._shared.bus.command.CommandBus;

public record UpdatePasswordCommand(String id, String currentPassword, String newPassword) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
}
