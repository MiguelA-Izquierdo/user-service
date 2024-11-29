package com.app.shopbackend._shared.bus.command;

public interface Command {
  void dispatch(CommandBus commandBus);
}
