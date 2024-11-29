package com.app.shopbackend.user.application.bus.command;

import com.app.shopbackend._shared.bus.command.Command;
import com.app.shopbackend._shared.bus.command.CommandBus;

public record CreateUserCommand(String id, String name, String lastName, String email, String password, String countryCode,
                                String number, String documentType, String documentNumber, String street,
                                String streetNumber, String city, String state, String postalCode,
                                String country) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
}
