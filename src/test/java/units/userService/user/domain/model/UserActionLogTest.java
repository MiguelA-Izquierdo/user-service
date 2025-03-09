package units.userService.user.domain.model;

import org.junit.jupiter.api.Assertions;
import units.userService._mocks.UserMockUtil;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserActionLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


class UserActionLogTest {
  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void testUserActionLogCreationWithoutTimestamp() {
    UUID id = UUID.randomUUID();
    User user = mockUser;
    UserAction action = UserAction.LOGGED_WITH_TOKEN;
    Map<String, String> metadata = Map.of("ip", "127.0.0.1");

    UserActionLog log = UserActionLog.of(id, user, action, metadata);

    Assertions.assertNotNull(log);
    Assertions.assertEquals(id, log.getId());
    Assertions.assertEquals(user, log.getUser());
    Assertions.assertEquals(action, log.getUserAction());
    Assertions.assertNotNull(log.getTimestamp());
    Assertions.assertEquals(metadata, log.getMetadata());
  }

  @Test
  void testUserActionLogCreationWithTimestamp() {
    UUID id = UUID.randomUUID();
    User user = mockUser;
    UserAction action = UserAction.LOGOUT;
    Instant timestamp = Instant.now();
    Map<String, String> metadata = Map.of("device", "mobile");

    UserActionLog log = UserActionLog.of(id, user, action, timestamp, metadata);

    Assertions.assertNotNull(log);
    Assertions.assertEquals(id, log.getId());
    Assertions.assertEquals(user, log.getUser());
    Assertions.assertEquals(action, log.getUserAction());
    Assertions.assertEquals(timestamp, log.getTimestamp());
    Assertions.assertEquals(metadata, log.getMetadata());
  }

  @Test
  void testMetadataIsImmutable() {
    UUID id = UUID.randomUUID();
    User user = mockUser;
    UserAction action = UserAction.LOGGED_WITH_TOKEN;
    Map<String, String> metadata = Map.of("browser", "Chrome");

    UserActionLog log = UserActionLog.of(id, user, action, metadata);

    Assertions.assertThrows(UnsupportedOperationException.class, () -> log.getMetadata().put("os", "Windows"));
  }

  @Test
  void testToString() {
    UUID id = UUID.randomUUID();
    User user = mockUser;
    UserAction action = UserAction.LOGGED_WITH_TOKEN;
    Instant timestamp = Instant.now();
    Map<String, String> metadata = Map.of("status", "success");

    UserActionLog log = UserActionLog.of(id, user, action, timestamp, metadata);
    String logString = log.toString();

    Assertions.assertTrue(logString.contains(id.toString()));
    Assertions.assertTrue(logString.contains(user.toString()));
    Assertions.assertTrue(logString.contains(action.toString()));
    Assertions.assertTrue(logString.contains(timestamp.toString()));
    Assertions.assertTrue(logString.contains(metadata.toString()));
  }
}
