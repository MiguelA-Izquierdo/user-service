package com.app.shopbackend._shared.bus.command;

public interface CommandBus {
  void dispatch(Command command);
}
