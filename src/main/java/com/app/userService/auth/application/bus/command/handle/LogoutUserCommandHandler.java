package com.app.userService.auth.application.bus.command.handle;

import com.app.userService._shared.bus.command.CommandHandler;
import com.app.userService.auth.application.bus.command.LogoutUserCommand;
import com.app.userService.auth.application.useCases.LogoutUseCase;
import com.app.userService.user.application.bus.command.DeleteUserCommand;
import com.app.userService.user.application.useCases.DeleteUserUseCase;
import org.springframework.stereotype.Service;


@Service
public class LogoutUserCommandHandler implements CommandHandler<LogoutUserCommand> {
  private final LogoutUseCase logoutUseCase;
  public LogoutUserCommandHandler(LogoutUseCase logoutUseCase) {
    this.logoutUseCase = logoutUseCase;
  }

  @Override
  public void handle(LogoutUserCommand command) {
    logoutUseCase.execute(command);
  }

  @Override
  public Class<LogoutUserCommand> getCommandType() {
    return LogoutUserCommand.class;
  }
}
