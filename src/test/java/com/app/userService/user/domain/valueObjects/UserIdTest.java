package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

  @Test
  void testValidUserIdFromUUID() {
    UUID uuid = UUID.randomUUID();
    UserId userId = UserId.of(uuid);
    assertNotNull(userId);
    assertEquals(uuid, userId.getValue());
  }

  @Test
  void testValidUserIdFromString() {
    String uuidString = UUID.randomUUID().toString();
    UserId userId = UserId.of(uuidString);
    assertNotNull(userId);
    assertEquals(UUID.fromString(uuidString), userId.getValue());
  }

  @Test
  void testInvalidUUIDVersion() {
    UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserId.of(uuid)
    );
    assertEquals("The provided UUID is not a version 4 UUID", exception.getMessage());
  }

  @Test
  void testInvalidUUIDString() {
    String invalidUUIDString = "invalid-uuid-string";
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserId.of(invalidUUIDString)
    );
    assertEquals("The provided UserId is not a valid UUID: invalid-uuid-string", exception.getMessage());
  }

  @Test
  void testNullUUID() {
    Exception exception = assertThrows(NullPointerException.class, () ->
      UserId.of((UUID) null)
    );
    assertEquals("UserId cannot be null", exception.getMessage());
  }

  @Test
  void testEmptyUUIDString() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserId.of("")
    );
    assertEquals("UserId cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullUUIDString() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserId.of((String) null)
    );
    assertEquals("UserId cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UUID uuid1 = UUID.randomUUID();
    UUID uuid2 = UUID.randomUUID();
    UserId userId1 = UserId.of(uuid1);
    UserId userId2 = UserId.of(uuid1);
    UserId userId3 = UserId.of(uuid2);

    assertEquals(userId1, userId2);
    assertNotEquals(userId1, userId3);
    assertEquals(userId1.hashCode(), userId2.hashCode());
    assertNotEquals(userId1.hashCode(), userId3.hashCode());
  }

  @Test
  void testToString() {
    UUID uuid = UUID.randomUUID();
    UserId userId = UserId.of(uuid);
    assertEquals(uuid.toString(), userId.toString());
  }
}

