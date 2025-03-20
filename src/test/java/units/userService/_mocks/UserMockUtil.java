package units.userService._mocks;

import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.domain.valueObjects.*;
import org.mockito.Mockito;
import com.app.userService.user.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserMockUtil {

  public static User createMockUser() {
    UserId userId = UserId.of(UUID.randomUUID().toString());
    IdentityDocument identityDocument = IdentityDocument.of("Passport", "123456789");
    Phone phone = Phone.of("+34", "633633633");
    Address address = Address.of("Main St", "123", "Springfield", "Illinois", "62704", "USA");

    return User.of(userId,
      UserName.of("John"),
      UserLastName.of("Doe"),
      UserEmail.of("johndoe@example.com"),
      identityDocument,
      phone,
      address,
      "password123",
      0,
      "SecrectTest",
      LocalDateTime.now(),
      UserStatus.ACTIVE,
      List.of(Role.ROLE_USER)
      );
  }

  public static CreateUserCommand createCreateUserCommand() {
    String id = UUID.randomUUID().toString();
    String name = "John";
    String lastName = "Doe";
    String email = "johndoe@example.com";
    String password = "password123";
    String countryCode = "+34";
    String number = "1234567890";
    String documentType = "Passport";
    String documentNumber = "123456789";
    String street = "Main St";
    String streetNumber = "123";
    String city = "Springfield";
    String state = "Illinois";
    String postalCode = "62704";
    String country = "USA";

    return new CreateUserCommand(
      id, name, lastName, email, password, countryCode, number, documentType,
      documentNumber, street, streetNumber, city, state, postalCode, country
    );
  }

  public static UpdateUserCommand createUpdateUserCommand(String userId) {
    UpdateUserCommand.Phone phone = new UpdateUserCommand.Phone("+34", "622622622");
    UpdateUserCommand.IdentityDocument identityDocument = new UpdateUserCommand.IdentityDocument("DNI", "73559527F");
    UpdateUserCommand.Address address = new UpdateUserCommand.Address(
      "Main St",
      "321",
      "Springfield",
      "Illinois",
      "62704",
      "USA"
    );

    return new UpdateUserCommand(
      userId,
      "New Name",
      "Last Name",
      identityDocument,
      phone,
      address);
  }
  public static UpdateUserCommand createUpdateUserCommandFromUser(User user) {
    UpdateUserCommand.Phone phone = new UpdateUserCommand.Phone(
      user.getPhone().getCountryCode(),
      user.getPhone().getNumber()
    );

    UpdateUserCommand.IdentityDocument identityDocument = new UpdateUserCommand.IdentityDocument(
      user.getIdentityDocument().getDocumentType(),
      user.getIdentityDocument().getDocumentNumber()
    );

    UpdateUserCommand.Address address = new UpdateUserCommand.Address(
      user.getAddress().getStreet(),
      user.getAddress().getNumber(),
      user.getAddress().getCity(),
      user.getAddress().getState(),
      user.getAddress().getPostalCode(),
      user.getAddress().getCountry()
    );

    return new UpdateUserCommand(
      user.getId().getValue().toString(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      identityDocument,
      phone,
      address
    );
  }

}
