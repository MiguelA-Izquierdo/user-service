package com.app.userService.user.application.bus.command;

import com.app.userService._shared.application.bus.command.Command;
import com.app.userService._shared.application.bus.command.CommandBus;
import com.app.userService._shared.application.bus.command.CommandHandler;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserCommandBus implements CommandBus {

  private final Map<Class<? extends Command>, CommandHandler> handlers = new HashMap<>();
  private final List<CommandHandler> handlerList;

  public UserCommandBus(List<CommandHandler> handlerList) {
    this.handlerList = handlerList;
  }

  @PostConstruct
  public void registerHandlers() {
    for (CommandHandler handler : handlerList) {
      Class<? extends Command> commandType = handler.getCommandType();
      registerHandler(commandType, handler);
    }
  }

  public <T extends Command> void registerHandler(Class<T> commandType, CommandHandler<T> handler) {
    handlers.put(commandType, handler);
  }

  @Override
  public void dispatch(Command command) {
    CommandHandler handler = handlers.get(command.getClass());
    if (handler != null) {
      handler.handle(command);
    } else {
      throw new IllegalArgumentException("No handler user found for command: " + command.getClass().getName());
    }
  }
}
