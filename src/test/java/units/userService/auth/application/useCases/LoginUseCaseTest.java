package units.userService.auth.application.useCases;

import com.app.userService._shared.application.service.UserEventService;
import com.app.userService._shared.infrastructure.exceptions.InvalidPasswordException;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import com.app.userService.auth.application.service.LoginService;
import com.app.userService.auth.application.useCases.LoginUseCase;
import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUseCaseTest {

  @Mock private UserServiceCore userServiceCore;
  @Mock private UserActionLogService userActionLogService;
  @Mock private AuthService authService;
  @Mock private LoginService loginService;
  @Mock private UserEventService userEventService;

  @InjectMocks private LoginUseCase loginUseCase;

  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void execute_validCredentials_returnsUserLoggedDTO() {
    LoginQuery query = new LoginQuery("johndoe@example.com", "ValidPass1!@");
    when(userServiceCore.findUserByEmail(query.email())).thenReturn(UserWrapper.active(mockUser));
    when(authService.generateToken(mockUser)).thenReturn(new AuthToken("jwt-token", new Date()));

    UserLoggedDTO result = loginUseCase.execute(query);

    assertNotNull(result);
    assertEquals("jwt-token", result.getToken());
  }

  @Test
  void execute_userNotFound_throwsInvalidPasswordException() {
    LoginQuery query = new LoginQuery("notfound@example.com", "ValidPass1!@");
    when(userServiceCore.findUserByEmail(query.email())).thenReturn(UserWrapper.notFound());

    assertThrows(InvalidPasswordException.class, () -> loginUseCase.execute(query));
    verify(loginService, never()).login(any(), any());
  }

  @Test
  void execute_userInactive_throwsInvalidPasswordException() {
    LoginQuery query = new LoginQuery("johndoe@example.com", "ValidPass1!@");
    when(userServiceCore.findUserByEmail(query.email())).thenReturn(UserWrapper.inactive());

    assertThrows(InvalidPasswordException.class, () -> loginUseCase.execute(query));
    verify(loginService, never()).login(any(), any());
  }

  @Test
  void execute_validLogin_callsLoginService() {
    LoginQuery query = new LoginQuery("johndoe@example.com", "ValidPass1!@");
    when(userServiceCore.findUserByEmail(query.email())).thenReturn(UserWrapper.active(mockUser));
    when(authService.generateToken(mockUser)).thenReturn(new AuthToken("jwt-token", new Date()));

    loginUseCase.execute(query);

    verify(loginService, times(1)).login(mockUser, query.password());
  }

  @Test
  void execute_validLogin_registersLoginAction() {
    LoginQuery query = new LoginQuery("johndoe@example.com", "ValidPass1!@");
    when(userServiceCore.findUserByEmail(query.email())).thenReturn(UserWrapper.active(mockUser));
    when(authService.generateToken(mockUser)).thenReturn(new AuthToken("jwt-token", new Date()));

    loginUseCase.execute(query);

    verify(userActionLogService, times(1)).registerUserAction(mockUser, UserAction.LOGGED);
  }

  @Test
  void execute_validLogin_triggersLoginEvent() {
    LoginQuery query = new LoginQuery("johndoe@example.com", "ValidPass1!@");
    when(userServiceCore.findUserByEmail(query.email())).thenReturn(UserWrapper.active(mockUser));
    when(authService.generateToken(mockUser)).thenReturn(new AuthToken("jwt-token", new Date()));

    loginUseCase.execute(query);

    verify(userEventService, times(1)).handleUserLoggedEvent(mockUser, false);
  }
}