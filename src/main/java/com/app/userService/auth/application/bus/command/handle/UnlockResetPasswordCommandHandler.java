package com.app.userService.auth.application.bus.command.handle;

import com.app.userService._shared.bus.command.CommandHandler;
import com.app.userService.auth.application.bus.command.LogoutUserCommand;
import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.auth.application.useCases.LogoutUseCase;
import com.app.userService.auth.application.useCases.UnlockResetPasswordUseCase;
import com.app.userService.auth.application.validation.UnlockResetPasswordValidator;
import org.springframework.stereotype.Service;


@Service
public class UnlockResetPasswordCommandHandler implements CommandHandler<UnlockResetPasswordCommand> {
  private final UnlockResetPasswordUseCase useCase;
  private final UnlockResetPasswordValidator validator;
  public UnlockResetPasswordCommandHandler(UnlockResetPasswordUseCase useCase, UnlockResetPasswordValidator validator) {
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
