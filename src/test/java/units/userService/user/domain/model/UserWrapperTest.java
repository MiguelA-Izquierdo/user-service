package units.userService.user.domain.model;

import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


class UserWrapperTest {
  private User user;
  @BeforeEach
  void setUp() {
    UserId userId = UserId.of(UUID.randomUUID());
    UserName userName = UserName.of("John");
    UserLastName userLastName = UserLastName.of("Doe");
    UserEmail userEmail = UserEmail.of("john.doe@example.com");
    IdentityDocument identityDocument = IdentityDocument.of("DNI", "45559495H");
    Phone phone = Phone.of("+34", "622655655");
    Address address = Address.of("Gran Vía", "5", "Madrid", "Comunidad de Madrid", "10005", "España");
    String password = "password123";
    Integer failedLoginAttempts = 0;
    String secretKey = "secretKey1234";
    LocalDateTime createdAt = LocalDateTime.now();
    UserStatus status = UserStatus.ACTIVE;
    Role role = Role.ROLE_USER;
    List<Role> roles = List.of(role);

    user = User.of(userId, userName, userLastName, userEmail, identityDocument, phone, address, password, failedLoginAttempts, secretKey,createdAt, status, roles);
  }
  @Test
  void testUserWrapperActive() {

    UserWrapper wrapper = UserWrapper.active(user);

    Assertions.assertTrue(wrapper.exists());
    Assertions.assertTrue(wrapper.isActive());
    Assertions.assertEquals(Optional.of(user), wrapper.getUser());
  }

  @Test
  void testUserWrapperInactive() {
    UserWrapper wrapper = UserWrapper.inactive();

    Assertions.assertTrue(wrapper.exists());
    Assertions.assertFalse(wrapper.isActive());
    Assertions.assertFalse(wrapper.getUser().isPresent());
  }

  @Test
  void testUserWrapperNotFound() {
    UserWrapper wrapper = UserWrapper.notFound();

    Assertions.assertFalse(wrapper.exists());
    Assertions.assertFalse(wrapper.isActive());
    Assertions.assertFalse(wrapper.getUser().isPresent());
  }

  @Test
  void testIsActiveWhenUserExistsButInactive() {
    UserWrapper wrapper = UserWrapper.inactive();

    Assertions.assertFalse(wrapper.isActive());
    Assertions.assertTrue(wrapper.exists());
  }

  @Test
  void testIsActiveWhenUserDoesNotExist() {
    UserWrapper wrapper = UserWrapper.notFound();

    Assertions.assertFalse(wrapper.isActive());
    Assertions.assertFalse(wrapper.exists());
    Assertions.assertFalse(wrapper.getUser().isPresent());
  }
}
