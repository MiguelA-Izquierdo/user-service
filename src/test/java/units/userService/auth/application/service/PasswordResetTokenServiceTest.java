package units.userService.auth.application.service;

import com.app.userService._shared.infrastructure.exceptions.EntityExistsException;
import com.app.userService.auth.application.service.PasswordResetTokenService;
import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.auth.domain.repositories.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetTokenServiceTest {

  @Mock private PasswordResetTokenRepository passwordResetTokenRepository;

  @InjectMocks private PasswordResetTokenService passwordRestTokenService;

  private PasswordResetToken token;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    token = PasswordResetToken.of(
      UUID.randomUUID(),
      UserMockUtil.createMockUser(),
      "sample-token",
      LocalDateTime.now().plusHours(1),
      false
    );
  }

  @Test
  void createPasswordResetToken_success_savesToken() {
    when(passwordResetTokenRepository.findById(token.getId())).thenReturn(Optional.empty());

    passwordRestTokenService.createPasswordResetToken(token);

    verify(passwordResetTokenRepository, times(1)).save(token);
  }

  @Test
  void createPasswordResetToken_tokenAlreadyExists_throwsEntityExistsException() {
    when(passwordResetTokenRepository.findById(token.getId())).thenReturn(Optional.of(token));

    assertThrows(EntityExistsException.class, () -> passwordRestTokenService.createPasswordResetToken(token));
    verify(passwordResetTokenRepository, never()).save(any());
  }

  @Test
  void findByToken_existingToken_returnsToken() {
    when(passwordResetTokenRepository.findByToken("sample-token")).thenReturn(Optional.of(token));

    Optional<PasswordResetToken> result = passwordRestTokenService.findByToken("sample-token");

    assertTrue(result.isPresent());
    assertEquals(token, result.get());
  }

  @Test
  void findByToken_unknownToken_returnsEmpty() {
    when(passwordResetTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

    Optional<PasswordResetToken> result = passwordRestTokenService.findByToken("unknown");

    assertFalse(result.isPresent());
  }

  @Test
  void markAsUsed_marksTokenAndSaves() {
    passwordRestTokenService.markAsUsed(token);

    assertTrue(token.isUsed());
    verify(passwordResetTokenRepository, times(1)).save(token);
  }
}