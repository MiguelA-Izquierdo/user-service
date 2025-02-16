package com.app.userService.user.domain.model;

import com.app.userService.user.domain.valueObjects.*;
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
    String secretKey = "secretKey123456";
    LocalDateTime createdAt = LocalDateTime.now();
    UserStatus status = UserStatus.ACTIVE;
    Role role = Role.ROLE_USER;
    List<Role> roles = List.of(role);

    user = User.of(userId, userName, userLastName, userEmail, identityDocument, phone, address, password, secretKey,createdAt, status, roles);
  }

  @Test
  void testUserCreationWithBuilder() {
    assertEquals(user.getId(), user.getId());
    assertEquals(user.getName(), user.getName());
    assertEquals(user.getLastName(), user.getLastName());
    assertEquals(user.getEmail(), user.getEmail());
    assertEquals(user.getIdentityDocument(), user.getIdentityDocument());
    assertEquals(user.getPhone(), user.getPhone());
    assertEquals(user.getAddress(), user.getAddress());
    assertEquals(user.getPassword(), user.getPassword());
    assertEquals(user.getCreatedAt(), user.getCreatedAt());
    assertEquals(user.getStatus(), user.getStatus());
    assertEquals(user.getRoles(), user.getRoles());
  }

  @Test
  void testUpdatePassword() {
    user.updatePassword("newPassword123");
    assertEquals("newPassword123", user.getPassword());
  }
}
