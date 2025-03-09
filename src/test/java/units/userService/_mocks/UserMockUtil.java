package units.userService._mocks;

import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.domain.valueObjects.*;
import jakarta.validation.constraints.Email;
import org.mockito.Mockito;
import com.app.userService.user.domain.model.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMockUtil {

  public static User createMockUser() {
    User mockUser = Mockito.mock(User.class);

    String userId = UUID.randomUUID().toString();
    UserId mockUserId = Mockito.mock(userId);
    Mockito.when(mockUserId.getValue()).thenReturn(UUID.randomUUID());
    Mockito.when(mockUser.getId()).thenReturn(mockUserId);

    UserName mockName = Mockito.mock(UserName.class);
    Mockito.when(mockName.getValue()).thenReturn("John");
    Mockito.when(mockUser.getName()).thenReturn(mockName);

    UserLastName mockLastName = Mockito.mock(UserLastName.class);
    Mockito.when(mockLastName.getValue()).thenReturn("Doe");
    Mockito.when(mockUser.getLastName()).thenReturn(mockLastName);

    UserEmail mockEmail = Mockito.mock(UserEmail.class);
    Mockito.when(mockEmail.getEmail()).thenReturn("johndoe@example.com");
    Mockito.when(mockUser.getEmail()).thenReturn(mockEmail);

    IdentityDocument mockIdentityDocument = Mockito.mock(IdentityDocument.class);
    Mockito.when(mockIdentityDocument.getDocumentType()).thenReturn("Passport");
    Mockito.when(mockIdentityDocument.getDocumentNumber()).thenReturn("123456789");
    Mockito.when(mockUser.getIdentityDocument()).thenReturn(mockIdentityDocument);

    Phone mockPhone = Mockito.mock(Phone.class);
    Mockito.when(mockPhone.getCountryCode()).thenReturn("+1");
    Mockito.when(mockPhone.getNumber()).thenReturn("1234567890");
    Mockito.when(mockUser.getPhone()).thenReturn(mockPhone);

    Address mockAddress = Mockito.mock(Address.class);
    Mockito.when(mockAddress.getStreet()).thenReturn("Main St");
    Mockito.when(mockAddress.getNumber()).thenReturn("123");
    Mockito.when(mockAddress.getCity()).thenReturn("Springfield");
    Mockito.when(mockAddress.getState()).thenReturn("Illinois");
    Mockito.when(mockAddress.getPostalCode()).thenReturn("62704");
    Mockito.when(mockAddress.getCountry()).thenReturn("USA");
    Mockito.when(mockUser.getAddress()).thenReturn(mockAddress);

    Mockito.when(mockUser.getPassword()).thenReturn("password123");
    Mockito.when(mockUser.getCreatedAt()).thenReturn(LocalDateTime.now());

    return mockUser;
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
      "123",
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
}
