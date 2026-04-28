package units.userService.auth.application.useCases;

import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.auth.application.bus.query.LoginWithTokenQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import com.app.userService.auth.application.useCases.LoginWithTokenUseCase;
import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginWithTokenUseCaseTest {

  @Mock private UserServiceCore userServiceCore;
  @Mock private AuthService authService;
  @Mock private UserActionLogService userActionLogService;
  @Mock private UserEventService userEventService;

  @InjectMocks private LoginWithTokenUseCase loginWithTokenUseCase;

  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void execute_validUser_returnsUserLoggedDTO() {
    String userId = mockUser.getId().getValue().toString();
    LoginWithTokenQuery query = new LoginWithTokenQuery(userId);
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.active(mockUser));
    when(authService.generateToken(mockUser)).thenReturn(new AuthToken("jwt-token", new Date()));

    UserLoggedDTO result = loginWithTokenUseCase.execute(query);

    assertNotNull(result);
    assertEquals("jwt-token", result.getToken());
  }

  @Test
  void execute_userNotFound_throwsEntityNotFoundException() {
    String userId = mockUser.getId().getValue().toString();
    LoginWithTokenQuery query = new LoginWithTokenQuery(userId);
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.notFound());

    assertThrows(EntityNotFoundException.class, () -> loginWithTokenUseCase.execute(query));
  }

  @Test
  void execute_userInactive_throwsEntityNotFoundException() {
    String userId = mockUser.getId().getValue().toString();
    LoginWithTokenQuery query = new LoginWithTokenQuery(userId);
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.inactive());

    assertThrows(EntityNotFoundException.class, () -> loginWithTokenUseCase.execute(query));
  }

  @Test
  void execute_validLogin_registersLoggedWithTokenAction() {
    String userId = mockUser.getId().getValue().toString();
    LoginWithTokenQuery query = new LoginWithTokenQuery(userId);
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.active(mockUser));
    when(authService.generateToken(mockUser)).thenReturn(new AuthToken("jwt-token", new Date()));

    loginWithTokenUseCase.execute(query);

    verify(userActionLogService, times(1)).registerUserAction(mockUser, UserAction.LOGGED_WITH_TOKEN);
  }

  @Test
  void execute_validLogin_triggersLoginEventWithTokenFlag() {
    String userId = mockUser.getId().getValue().toString();
    LoginWithTokenQuery query = new LoginWithTokenQuery(userId);
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.active(mockUser));
    when(authService.generateToken(mockUser)).thenReturn(new AuthToken("jwt-token", new Date()));

    loginWithTokenUseCase.execute(query);

    verify(userEventService, times(1)).handleUserLoggedEvent(mockUser, true);
  }
}