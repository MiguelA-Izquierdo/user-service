package com.app.userService._shared.bus.command;

public interface CommandBus {
  void dispatch(Command command);
}
