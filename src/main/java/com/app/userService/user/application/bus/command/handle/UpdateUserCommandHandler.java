package com.app.userService.user.application.bus.command.handle;

import com.app.userService._shared.application.bus.command.CommandHandler;
import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.application.useCases.UpdateUserUseCase;
import com.app.userService.user.application.validation.UpdateUserCommandValidator;
import org.springframework.stereotype.Service;


@Service
public class UpdateUserCommandHandler implements CommandHandler<UpdateUserCommand> {
  private final UpdateUserCommandValidator updateUserCommandValidator;
  private final UpdateUserUseCase updateUserUseCase;
  public UpdateUserCommandHandler(UpdateUserCommandValidator updateUserCommandValidator,UpdateUserUseCase updateUserUseCase) {
    this.updateUserCommandValidator = updateUserCommandValidator;
    this.updateUserUseCase = updateUserUseCase;
  }

  @Override
  public void handle(UpdateUserCommand command) {
    updateUserCommandValidator.validate(command);
    updateUserUseCase.execute(command);
  }

  @Override
  public Class<UpdateUserCommand> getCommandType() {
    return UpdateUserCommand.class;
  }
}
