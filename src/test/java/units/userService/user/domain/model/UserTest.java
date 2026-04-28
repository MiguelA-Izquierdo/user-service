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
  private UserId userId;
  private UserName userName;
  private UserLastName userLastName;
  private UserEmail userEmail;
  private IdentityDocument identityDocument;
  private Phone phone;
  private Address address;
  private String password;
  private Integer failedLoginAttempts;
  private String secretKey;
  private LocalDateTime createdAt;
  private UserStatus status;
  private List<Role> roles;

  @BeforeEach
  void setUp() {
    userId = UserId.of(UUID.randomUUID());
    userName = UserName.of("John");
    userLastName = UserLastName.of("Doe");
    userEmail = UserEmail.of("john.doe@example.com");
    identityDocument = IdentityDocument.of("DNI", "45559495H");
    phone = Phone.of("+34", "622655655");
    address = Address.of("Gran Vía", "5", "Madrid", "Comunidad de Madrid", "10005", "España");
    password = "password123";
    failedLoginAttempts = 0;
    secretKey = "secretKey123456";
    createdAt = LocalDateTime.now();
    status = UserStatus.ACTIVE;
    roles = List.of(Role.ROLE_USER);

    user = User.of(userId, userName, userLastName, userEmail, identityDocument, phone, address, password, failedLoginAttempts, secretKey, createdAt, status, roles);
  }

  @Test
  void testUserCreationWithBuilder() {
    assertEquals(userId, user.getId());
    assertEquals(userName, user.getName());
    assertEquals(userLastName, user.getLastName());
    assertEquals(userEmail, user.getEmail());
    assertEquals(identityDocument, user.getIdentityDocument());
    assertEquals(phone, user.getPhone());
    assertEquals(address, user.getAddress());
    assertEquals(password, user.getPassword());
    assertEquals(secretKey, user.getSecretKey());
    assertEquals(failedLoginAttempts, user.getFailedLoginAttempts());
    assertEquals(createdAt, user.getCreatedAt());
    assertEquals(status, user.getStatus());
    assertEquals(roles, user.getRoles());
  }

  @Test
  void testUpdatePassword() {
    user.updatePassword("newPassword123");
    Assertions.assertEquals("newPassword123", user.getPassword());
  }

  @Test
  void testUpdatePassword_regeneratesSecretKey() {
    String originalKey = user.getSecretKey();
    user.updatePassword("newPassword123");
    assertNotEquals(originalKey, user.getSecretKey());
  }

  @Test
  void testRegisterFailedLoginAttempt_incrementsCounter() {
    int before = user.getFailedLoginAttempts();
    user.registerFailedLoginAttempt();
    assertEquals(before + 1, user.getFailedLoginAttempts());
  }

  @Test
  void testRegisterFailedLoginAttempt_locksAccountAfterFiveAttempts() {
    assertFalse(user.isLocked());
    for (int i = 0; i < 5; i++) {
      user.registerFailedLoginAttempt();
    }
    assertTrue(user.isLocked());
  }

  @Test
  void testRegisterFailedLoginAttempt_doesNotLockBeforeFiveAttempts() {
    for (int i = 0; i < 4; i++) {
      user.registerFailedLoginAttempt();
    }
    assertFalse(user.isLocked());
  }

  @Test
  void testIsLocked_returnsFalseForActiveUser() {
    assertFalse(user.isLocked());
  }

  @Test
  void testClearFailedLoginAttempts_resetsCounterToZero() {
    user.registerFailedLoginAttempt();
    user.registerFailedLoginAttempt();
    user.clearFailedLoginAttempts();
    assertEquals(0, user.getFailedLoginAttempts());
  }

  @Test
  void testLogout_regeneratesSecretKey() {
    String originalKey = user.getSecretKey();
    user.logout();
    assertNotEquals(originalKey, user.getSecretKey());
  }

  @Test
  void testUnlockAccount_setsStatusToActiveAndClearsAttempts() {
    for (int i = 0; i < 5; i++) {
      user.registerFailedLoginAttempt();
    }
    assertTrue(user.isLocked());

    user.unlockAccount();

    assertFalse(user.isLocked());
    assertEquals(0, user.getFailedLoginAttempts());
  }
}
