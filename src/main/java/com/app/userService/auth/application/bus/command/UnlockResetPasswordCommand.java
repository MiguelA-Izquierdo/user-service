package com.app.userService.auth.application.bus.command;

import com.app.userService._shared.application.bus.command.Command;
import com.app.userService._shared.application.bus.command.CommandBus;

public record UnlockResetPasswordCommand(String token, String newPassword) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
}
