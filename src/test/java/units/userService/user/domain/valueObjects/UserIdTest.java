package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.UserId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class UserIdTest {

  @Test
  void testValidUserIdFromUUID() {
    UUID uuid = UUID.randomUUID();
    UserId userId = UserId.of(uuid);
    Assertions.assertNotNull(userId);
    Assertions.assertEquals(uuid, userId.getValue());
  }

  @Test
  void testValidUserIdFromString() {
    String uuidString = UUID.randomUUID().toString();
    UserId userId = UserId.of(uuidString);
    Assertions.assertNotNull(userId);
    Assertions.assertEquals(UUID.fromString(uuidString), userId.getValue());
  }

  @Test
  void testInvalidUUIDVersion() {
    UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserId.of(uuid)
    );
    Assertions.assertEquals("The provided UUID is not a version 4 UUID", exception.getMessage());
  }

  @Test
  void testInvalidUUIDString() {
    String invalidUUIDString = "invalid-uuid-string";
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserId.of(invalidUUIDString)
    );
    Assertions.assertEquals("The provided UserId is not a valid UUID: invalid-uuid-string", exception.getMessage());
  }

  @Test
  void testNullUUID() {
    Exception exception = Assertions.assertThrows(NullPointerException.class, () ->
      UserId.of((UUID) null)
    );
    Assertions.assertEquals("UserId cannot be null", exception.getMessage());
  }

  @Test
  void testEmptyUUIDString() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserId.of("")
    );
    Assertions.assertEquals("UserId cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullUUIDString() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserId.of((String) null)
    );
    Assertions.assertEquals("UserId cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UUID uuid1 = UUID.randomUUID();
    UUID uuid2 = UUID.randomUUID();
    UserId userId1 = UserId.of(uuid1);
    UserId userId2 = UserId.of(uuid1);
    UserId userId3 = UserId.of(uuid2);

    Assertions.assertEquals(userId1, userId2);
    Assertions.assertNotEquals(userId1, userId3);
    Assertions.assertEquals(userId1.hashCode(), userId2.hashCode());
    Assertions.assertNotEquals(userId1.hashCode(), userId3.hashCode());
  }

  @Test
  void testToString() {
    UUID uuid = UUID.randomUUID();
    UserId userId = UserId.of(uuid);
    Assertions.assertEquals(uuid.toString(), userId.toString());
  }
}

