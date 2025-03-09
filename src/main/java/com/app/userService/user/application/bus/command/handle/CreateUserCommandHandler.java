package com.app.userService.user.application.bus.command.handle;

import com.app.userService._shared.application.bus.command.CommandHandler;
import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.application.useCases.CreateUserUseCase;
import com.app.userService.user.application.validation.CreateUserCommandValidator;
import org.springframework.stereotype.Service;


@Service
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand> {
  private final CreateUserCommandValidator createUserCommandValidator;
  private final CreateUserUseCase createUserUseCase;

  public CreateUserCommandHandler(CreateUserCommandValidator createUserCommandValidator,
                                  CreateUserUseCase createUserUseCase) {
    this.createUserCommandValidator = createUserCommandValidator;
    this.createUserUseCase = createUserUseCase;
  }

  @Override
  public void handle(CreateUserCommand command) {
    createUserCommandValidator.validate(command);
    createUserUseCase.execute(command);
  }

  @Override
  public Class<CreateUserCommand> getCommandType() {
    return CreateUserCommand.class;
  }
}
