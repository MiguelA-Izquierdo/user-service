package com.app.userService.user.application.bus.command;

import com.app.userService._shared.bus.command.Command;
import com.app.userService._shared.bus.command.CommandBus;

public record CreateUserCommand(String id, String name, String lastName, String email, String password, String countryCode,
                                String number, String documentType, String documentNumber, String street,
                                String streetNumber, String city, String state, String postalCode,
                                String country) implements Command {

  public void dispatch(CommandBus commandBus) {
  }
}
