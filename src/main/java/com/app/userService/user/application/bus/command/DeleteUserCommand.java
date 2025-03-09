package com.app.userService.user.application.bus.command;

import com.app.userService._shared.application.bus.command.Command;
import com.app.userService._shared.application.bus.command.CommandBus;

public record DeleteUserCommand(String id) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
}
