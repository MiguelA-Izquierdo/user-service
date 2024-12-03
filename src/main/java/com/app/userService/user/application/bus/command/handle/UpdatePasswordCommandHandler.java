package com.app.userService.user.application.bus.command.handle;

import com.app.userService._shared.bus.command.CommandHandler;
import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.application.useCases.UpdatePasswordUseCase;
import com.app.userService.user.application.validation.UpdatePasswordCommandValidator;
import org.springframework.stereotype.Service;


@Service
public class UpdatePasswordCommandHandler implements CommandHandler<UpdatePasswordCommand> {
  private final UpdatePasswordCommandValidator updatePasswordCommandValidator;
  private final UpdatePasswordUseCase updatePasswordUseCase;

  public UpdatePasswordCommandHandler(UpdatePasswordCommandValidator updatePasswordCommandValidator,
                                      UpdatePasswordUseCase updatePasswordUseCase) {
    this.updatePasswordCommandValidator = updatePasswordCommandValidator;
    this.updatePasswordUseCase = updatePasswordUseCase;
  }

  @Override
  public void handle(UpdatePasswordCommand command) {
    this.updatePasswordCommandValidator.validate(command);
    updatePasswordUseCase.execute(command);
  }

  @Override
  public Class<UpdatePasswordCommand> getCommandType() {
    return UpdatePasswordCommand.class;
  }
}
