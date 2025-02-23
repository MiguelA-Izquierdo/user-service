package com.app.userService.user.domain.model;

import com.app.userService.user.domain.valueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    assertTrue(wrapper.exists());
    assertTrue(wrapper.isActive());
    assertEquals(Optional.of(user), wrapper.getUser());
  }

  @Test
  void testUserWrapperInactive() {
    UserWrapper wrapper = UserWrapper.inactive();

    assertTrue(wrapper.exists());
    assertFalse(wrapper.isActive());
    assertFalse(wrapper.getUser().isPresent());
  }

  @Test
  void testUserWrapperNotFound() {
    UserWrapper wrapper = UserWrapper.notFound();

    assertFalse(wrapper.exists());
    assertFalse(wrapper.isActive());
    assertFalse(wrapper.getUser().isPresent());
  }

  @Test
  void testIsActiveWhenUserExistsButInactive() {
    UserWrapper wrapper = UserWrapper.inactive();

    assertFalse(wrapper.isActive());
    assertTrue(wrapper.exists());
  }

  @Test
  void testIsActiveWhenUserDoesNotExist() {
    UserWrapper wrapper = UserWrapper.notFound();

    assertFalse(wrapper.isActive());
    assertFalse(wrapper.exists());
    assertFalse(wrapper.getUser().isPresent());
  }
}
