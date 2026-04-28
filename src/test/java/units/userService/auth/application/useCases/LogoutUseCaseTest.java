package units.userService.auth.application.useCases;

import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.auth.application.bus.command.LogoutUserCommand;
import com.app.userService.auth.application.service.LoginService;
import com.app.userService.auth.application.useCases.LogoutUseCase;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogoutUseCaseTest {

  @Mock private UserServiceCore userServiceCore;
  @Mock private UserActionLogService userActionLogService;
  @Mock private LoginService loginService;
  @Mock private UserEventService userEventService;

  @InjectMocks private LogoutUseCase logoutUseCase;

  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void execute_validUser_callsLogoutService() {
    String userId = mockUser.getId().getValue().toString();
    LogoutUserCommand command = new LogoutUserCommand(userId, "executorId");
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.active(mockUser));

    logoutUseCase.execute(command);

    verify(loginService, times(1)).logoutUser(mockUser);
  }

  @Test
  void execute_userNotFound_throwsEntityNotFoundException() {
    String userId = mockUser.getId().getValue().toString();
    LogoutUserCommand command = new LogoutUserCommand(userId, "executorId");
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.notFound());

    assertThrows(EntityNotFoundException.class, () -> logoutUseCase.execute(command));
    verify(loginService, never()).logoutUser(any());
  }

  @Test
  void execute_userInactive_throwsEntityNotFoundException() {
    String userId = mockUser.getId().getValue().toString();
    LogoutUserCommand command = new LogoutUserCommand(userId, "executorId");
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.inactive());

    assertThrows(EntityNotFoundException.class, () -> logoutUseCase.execute(command));
    verify(loginService, never()).logoutUser(any());
  }

  @Test
  void execute_validLogout_registersLogoutAction() {
    String userId = mockUser.getId().getValue().toString();
    LogoutUserCommand command = new LogoutUserCommand(userId, "executorId");
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.active(mockUser));

    logoutUseCase.execute(command);

    verify(userActionLogService, times(1)).registerUserAction(mockUser, UserAction.LOGOUT);
  }

  @Test
  void execute_validLogout_triggersLogoutEvent() {
    String userId = mockUser.getId().getValue().toString();
    LogoutUserCommand command = new LogoutUserCommand(userId, "executorId");
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(UserWrapper.active(mockUser));

    logoutUseCase.execute(command);

    verify(userEventService, times(1)).handleUserLogoutEvent(mockUser);
  }
}