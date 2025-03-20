package units.userService.user.application.service;


import com.app.userService._shared.application.service.RequestContext;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserActionLog;
import com.app.userService.user.domain.repositories.UserActionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import units.userService._mocks.UserMockUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserActionLogServiceTest {
  @Mock
  private UserActionLogRepository userActionLogRepository;

  @Mock
  private RequestContext requestContext;

  @InjectMocks
  private UserActionLogService userActionLogService;

  private User user;
  private UserAction action;

  @BeforeEach
  void setUp() {
    user = UserMockUtil.createMockUser();
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void registerUserAction_ShouldSaveActionWithMergedMetadata() {
    Map<String, String> requestMetaData = new HashMap<>();
    requestMetaData.put("ip", "192.168.1.1");
    when(requestContext.getMetaData()).thenReturn(requestMetaData);

    userActionLogService.addMetadata("browser", "Chrome");

    userActionLogService.registerUserAction(user, action);

    ArgumentCaptor<UserActionLog> captor = ArgumentCaptor.forClass(UserActionLog.class);
    verify(userActionLogRepository).save(captor.capture());

    UserActionLog savedLog = captor.getValue();
    assertNotNull(savedLog);
    assertEquals(user, savedLog.getUser());
    assertEquals(action, savedLog.getUserAction());

    Map<String, String> expectedMetadata = new HashMap<>();
    expectedMetadata.put("ip", "192.168.1.1");
    expectedMetadata.put("browser", "Chrome");
    assertEquals(expectedMetadata, savedLog.getMetadata());
  }

  @Test
  void addMetadata_ShouldStoreMetadata() {
    userActionLogService.addMetadata("key1", "value1");
    userActionLogService.addMetadata("key2", "value2");

    userActionLogService.registerUserAction(user, action);

    ArgumentCaptor<UserActionLog> captor = ArgumentCaptor.forClass(UserActionLog.class);
    verify(userActionLogRepository).save(captor.capture());

    Map<String, String> savedMetadata = captor.getValue().getMetadata();
    assertEquals(2, savedMetadata.size());
    assertEquals("value1", savedMetadata.get("key1"));
    assertEquals("value2", savedMetadata.get("key2"));
  }

  @Test
  void registerUserAction_ShouldClearMetadataAfterSaving() {
    userActionLogService.addMetadata("tempKey", "tempValue");
    userActionLogService.registerUserAction(user, action);

    userActionLogService.registerUserAction(user, action);

    ArgumentCaptor<UserActionLog> captor = ArgumentCaptor.forClass(UserActionLog.class);
    verify(userActionLogRepository, times(2)).save(captor.capture());

    assertTrue(captor.getAllValues().get(1).getMetadata().isEmpty());
  }
}
