package units.userService.auth.application.service;

import com.app.userService._shared.application.service.UserEventService;
import com.app.userService._shared.infrastructure.exceptions.InvalidPasswordException;
import com.app.userService._shared.infrastructure.exceptions.UserLockedException;
import com.app.userService.auth.application.service.LoginService;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

  @Mock private UserPasswordService userPasswordService;
  @Mock private UserEventService userEventService;
  @Mock private UserActionLogService userActionLogService;
  @Mock private UserRepository userRepository;

  @InjectMocks private LoginService loginService;

  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void login_validCredentials_clearsFailedAttempts() {
    when(userPasswordService.isPasswordValid("ValidPass1!@", mockUser.getPassword())).thenReturn(true);

    loginService.login(mockUser, "ValidPass1!@");

    assertEquals(0, mockUser.getFailedLoginAttempts());
    verify(userRepository, times(1)).save(mockUser);
  }

  @Test
  void login_lockedUser_throwsUserLockedExceptionWithoutCheckingPassword() {
    for (int i = 0; i < 5; i++) {
      mockUser.registerFailedLoginAttempt();
    }
    assertTrue(mockUser.isLocked());

    assertThrows(UserLockedException.class, () -> loginService.login(mockUser, "anyPassword"));
    verify(userPasswordService, never()).isPasswordValid(any(), any());
  }

  @Test
  void login_invalidPassword_throwsInvalidPasswordException() {
    when(userPasswordService.isPasswordValid("wrongPass", mockUser.getPassword())).thenReturn(false);

    assertThrows(InvalidPasswordException.class, () -> loginService.login(mockUser, "wrongPass"));
  }

  @Test
  void login_invalidPassword_incrementsFailedLoginAttempts() {
    when(userPasswordService.isPasswordValid("wrongPass", mockUser.getPassword())).thenReturn(false);
    int before = mockUser.getFailedLoginAttempts();

    assertThrows(InvalidPasswordException.class, () -> loginService.login(mockUser, "wrongPass"));

    assertEquals(before + 1, mockUser.getFailedLoginAttempts());
  }

  @Test
  void login_fiveConsecutiveFailures_locksAccountAndThrowsUserLockedException() {
    when(userPasswordService.isPasswordValid(anyString(), any())).thenReturn(false);

    for (int i = 0; i < 4; i++) {
      assertThrows(InvalidPasswordException.class, () -> loginService.login(mockUser, "wrongPass"));
    }
    assertFalse(mockUser.isLocked());

    assertThrows(UserLockedException.class, () -> loginService.login(mockUser, "wrongPass"));
    assertTrue(mockUser.isLocked());
  }

  @Test
  void login_accountLockedOnLastAttempt_registersLockedAction() {
    when(userPasswordService.isPasswordValid(anyString(), any())).thenReturn(false);

    for (int i = 0; i < 4; i++) {
      try { loginService.login(mockUser, "wrongPass"); } catch (Exception ignored) {}
    }

    assertThrows(UserLockedException.class, () -> loginService.login(mockUser, "wrongPass"));

    verify(userActionLogService, times(1)).registerUserAction(mockUser, com.app.userService.user.domain.model.UserAction.LOCKED);
  }

  @Test
  void logoutUser_regeneratesSecretKey() {
    String originalSecretKey = mockUser.getSecretKey();

    loginService.logoutUser(mockUser);

    assertNotEquals(originalSecretKey, mockUser.getSecretKey());
    verify(userRepository, times(1)).save(mockUser);
  }
}