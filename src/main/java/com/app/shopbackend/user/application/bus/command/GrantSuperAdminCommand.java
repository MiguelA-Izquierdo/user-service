package com.app.shopbackend.user.application.bus.command;

import com.app.shopbackend._shared.bus.command.Command;
import com.app.shopbackend._shared.bus.command.CommandBus;
public record GrantSuperAdminCommand(String id) implements Command {
  public void dispatch(CommandBus commandBus) {
  }
}
