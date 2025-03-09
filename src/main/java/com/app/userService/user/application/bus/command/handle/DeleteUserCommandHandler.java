package com.app.userService.user.application.bus.command.handle;

import com.app.userService._shared.application.bus.command.CommandHandler;
import com.app.userService.user.application.bus.command.DeleteUserCommand;
import com.app.userService.user.application.useCases.DeleteUserUseCase;

import org.springframework.stereotype.Service;


@Service
public class DeleteUserCommandHandler implements CommandHandler<DeleteUserCommand> {
  private final DeleteUserUseCase deleteUserUseCase;
  public DeleteUserCommandHandler(DeleteUserUseCase deleteUserUseCase) {
    this.deleteUserUseCase = deleteUserUseCase;
  }

  @Override
  public void handle(DeleteUserCommand command) {
    deleteUserUseCase.execute(command);
  }

  @Override
  public Class<DeleteUserCommand> getCommandType() {
    return DeleteUserCommand.class;
  }
}
