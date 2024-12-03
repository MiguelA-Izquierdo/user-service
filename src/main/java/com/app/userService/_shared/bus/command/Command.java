package com.app.userService._shared.bus.command;

public interface Command {
  void dispatch(CommandBus commandBus);
}
