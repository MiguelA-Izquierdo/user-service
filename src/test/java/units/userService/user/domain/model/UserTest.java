package units.userService.user.domain.model;

import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.valueObjects.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class UserTest {

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
    String secretKey = "secretKey123456";
    LocalDateTime createdAt = LocalDateTime.now();
    UserStatus status = UserStatus.ACTIVE;
    Role role = Role.ROLE_USER;
    List<Role> roles = List.of(role);

    user = User.of(userId, userName, userLastName, userEmail, identityDocument, phone, address, password, failedLoginAttempts, secretKey,createdAt, status, roles);
  }

  @Test
  void testUserCreationWithBuilder() {
    Assertions.assertEquals(user.getId(), user.getId());
    Assertions.assertEquals(user.getName(), user.getName());
    Assertions.assertEquals(user.getLastName(), user.getLastName());
    Assertions.assertEquals(user.getEmail(), user.getEmail());
    Assertions.assertEquals(user.getIdentityDocument(), user.getIdentityDocument());
    Assertions.assertEquals(user.getPhone(), user.getPhone());
    Assertions.assertEquals(user.getAddress(), user.getAddress());
    Assertions.assertEquals(user.getPassword(), user.getPassword());
    Assertions.assertEquals(user.getCreatedAt(), user.getCreatedAt());
    Assertions.assertEquals(user.getStatus(), user.getStatus());
    Assertions.assertEquals(user.getRoles(), user.getRoles());
  }

  @Test
  void testUpdatePassword() {
    user.updatePassword("newPassword123");
    Assertions.assertEquals("newPassword123", user.getPassword());
  }
}
