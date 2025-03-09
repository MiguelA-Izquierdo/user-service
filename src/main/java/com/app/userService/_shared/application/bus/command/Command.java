package com.app.userService._shared.application.bus.command;

public interface Command {
  void dispatch(CommandBus commandBus);
}
