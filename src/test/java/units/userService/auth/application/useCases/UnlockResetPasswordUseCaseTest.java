package units.userService.auth.application.useCases;

import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.auth.application.service.PasswordResetTokenService;
import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.auth.application.useCases.UnlockResetPasswordUseCase;
import com.app.userService.auth.domain.exceptions.TokenExpiredException;
import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnlockResetPasswordUseCaseTest {

  @Mock private UserServiceCore userServiceCore;
  @Mock private UserPasswordService userPasswordService;
  @Mock private PasswordResetTokenService passwordRestTokenService;
  @Mock private UserActionLogService userActionLogService;
  @Mock private UserEventService userEventService;

  @InjectMocks private UnlockResetPasswordUseCase unlockResetPasswordUseCase;

  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void execute_validToken_marksTokenAsUsed() {
    String tokenValue = "valid-token";
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(tokenValue, "ValidPass1!@");
    PasswordResetToken token = PasswordResetToken.of(UUID.randomUUID(), mockUser, tokenValue, LocalDateTime.now().plusHours(1), false);
    when(passwordRestTokenService.findByToken(tokenValue)).thenReturn(Optional.of(token));

    unlockResetPasswordUseCase.execute(command);

    verify(passwordRestTokenService, times(1)).markAsUsed(token);
  }

  @Test
  void execute_validToken_resetsPassword() {
    String tokenValue = "valid-token";
    String newPassword = "ValidPass1!@";
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(tokenValue, newPassword);
    PasswordResetToken token = PasswordResetToken.of(UUID.randomUUID(), mockUser, tokenValue, LocalDateTime.now().plusHours(1), false);
    when(passwordRestTokenService.findByToken(tokenValue)).thenReturn(Optional.of(token));

    unlockResetPasswordUseCase.execute(command);

    verify(userPasswordService, times(1)).resetPassword(mockUser, newPassword);
  }

  @Test
  void execute_validToken_unlocksAccount() {
    String tokenValue = "valid-token";
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(tokenValue, "ValidPass1!@");
    PasswordResetToken token = PasswordResetToken.of(UUID.randomUUID(), mockUser, tokenValue, LocalDateTime.now().plusHours(1), false);
    when(passwordRestTokenService.findByToken(tokenValue)).thenReturn(Optional.of(token));

    unlockResetPasswordUseCase.execute(command);

    verify(userServiceCore, times(1)).unlockAccount(mockUser);
  }

  @Test
  void execute_tokenNotFound_throwsEntityNotFoundException() {
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand("unknown-token", "ValidPass1!@");
    when(passwordRestTokenService.findByToken("unknown-token")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> unlockResetPasswordUseCase.execute(command));
    verify(userPasswordService, never()).resetPassword(any(), any());
  }

  @Test
  void execute_expiredToken_throwsTokenExpiredException() {
    String tokenValue = "expired-token";
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(tokenValue, "ValidPass1!@");
    PasswordResetToken expiredToken = PasswordResetToken.of(UUID.randomUUID(), mockUser, tokenValue, LocalDateTime.now().minusHours(1), false);
    when(passwordRestTokenService.findByToken(tokenValue)).thenReturn(Optional.of(expiredToken));

    assertThrows(TokenExpiredException.class, () -> unlockResetPasswordUseCase.execute(command));
    verify(userPasswordService, never()).resetPassword(any(), any());
  }

  @Test
  void execute_usedToken_throwsTokenExpiredException() {
    String tokenValue = "used-token";
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(tokenValue, "ValidPass1!@");
    PasswordResetToken usedToken = PasswordResetToken.of(UUID.randomUUID(), mockUser, tokenValue, LocalDateTime.now().plusHours(1), true);
    when(passwordRestTokenService.findByToken(tokenValue)).thenReturn(Optional.of(usedToken));

    assertThrows(TokenExpiredException.class, () -> unlockResetPasswordUseCase.execute(command));
  }

  @Test
  void execute_validToken_registersUnlockAction() {
    String tokenValue = "valid-token";
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(tokenValue, "ValidPass1!@");
    PasswordResetToken token = PasswordResetToken.of(UUID.randomUUID(), mockUser, tokenValue, LocalDateTime.now().plusHours(1), false);
    when(passwordRestTokenService.findByToken(tokenValue)).thenReturn(Optional.of(token));

    unlockResetPasswordUseCase.execute(command);

    verify(userActionLogService, times(1)).registerUserAction(mockUser, UserAction.UNLOCKED);
  }

  @Test
  void execute_validToken_publishesUnlockedEvent() {
    String tokenValue = "valid-token";
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(tokenValue, "ValidPass1!@");
    PasswordResetToken token = PasswordResetToken.of(UUID.randomUUID(), mockUser, tokenValue, LocalDateTime.now().plusHours(1), false);
    when(passwordRestTokenService.findByToken(tokenValue)).thenReturn(Optional.of(token));

    unlockResetPasswordUseCase.execute(command);

    verify(userEventService, times(1)).handleUserUnlockedEvent(mockUser);
  }
}