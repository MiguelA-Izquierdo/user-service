package com.app.userService.auth.application.bus.command.handle;

import com.app.userService._shared.application.bus.command.CommandHandler;
import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.auth.application.useCases.UnlockResetPasswordUseCase;
import com.app.userService.auth.application.validation.UnlockResetPasswordCommandValidator;
import org.springframework.stereotype.Service;


@Service
public class UnlockResetPasswordCommandHandler implements CommandHandler<UnlockResetPasswordCommand> {
  private final UnlockResetPasswordUseCase useCase;
  private final UnlockResetPasswordCommandValidator validator;
  public UnlockResetPasswordCommandHandler(UnlockResetPasswordUseCase useCase, UnlockResetPasswordCommandValidator validator) {
    this.useCase = useCase;
    this.validator = validator;
  }

  @Override
  public void handle(UnlockResetPasswordCommand command) {
    validator.validate(command);
    useCase.execute(command);
  }

  @Override
  public Class<UnlockResetPasswordCommand> getCommandType() {
    return UnlockResetPasswordCommand.class;
  }
}
