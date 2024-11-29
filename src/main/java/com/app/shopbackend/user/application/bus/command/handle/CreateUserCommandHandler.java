package com.app.shopbackend.user.application.bus.command.handle;

import com.app.shopbackend._shared.bus.command.CommandHandler;
import com.app.shopbackend.user.application.bus.command.CreateUserCommand;
import com.app.shopbackend.user.application.useCases.CreateUserUseCase;
import com.app.shopbackend.user.application.validation.CreateUserCommandValidator;
import com.app.shopbackend.user.domain.model.User;
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
    User user = this.createUserCommandValidator.validate(command);
    createUserUseCase.execute(user);
  }

  @Override
  public Class<CreateUserCommand> getCommandType() {
    return CreateUserCommand.class;
  }
}
