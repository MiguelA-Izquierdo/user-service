package com.app.userService._shared.application.bus.command;

public interface CommandHandler<T extends Command> {
  void handle(T command);
  Class<T> getCommandType();
}
