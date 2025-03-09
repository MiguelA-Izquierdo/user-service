package com.app.userService._shared.application.bus.command;

public interface CommandBus {
  void dispatch(Command command);
}
