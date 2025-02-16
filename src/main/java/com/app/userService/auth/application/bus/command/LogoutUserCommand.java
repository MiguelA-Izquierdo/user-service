package com.app.userService.auth.application.bus.command;

import com.app.userService._shared.bus.command.Command;
import com.app.userService._shared.bus.command.CommandBus;

public record LogoutUserCommand(String userId, String executorUserId) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
}
