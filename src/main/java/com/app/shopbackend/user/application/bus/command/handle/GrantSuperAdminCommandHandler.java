package com.app.shopbackend.user.application.bus.command.handle;

import com.app.shopbackend._shared.bus.command.CommandHandler;
import com.app.shopbackend.user.application.bus.command.GrantSuperAdminCommand;
import com.app.shopbackend.user.application.useCases.GrantSuperAdminUseCase;
import org.springframework.stereotype.Service;


@Service
public class GrantSuperAdminCommandHandler implements CommandHandler<GrantSuperAdminCommand> {
  private final GrantSuperAdminUseCase grantSuperAdminUseCase;

  public GrantSuperAdminCommandHandler(GrantSuperAdminUseCase grantSuperAdminUseCase) {
    this.grantSuperAdminUseCase = grantSuperAdminUseCase;
  }

  @Override
  public void handle(GrantSuperAdminCommand command) {
    this.grantSuperAdminUseCase.execute(command);
  }

  @Override
  public Class<GrantSuperAdminCommand> getCommandType() {
    return GrantSuperAdminCommand.class;
  }
}
