package units.userService.auth.domain.model;

import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.user.domain.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import units.userService._mocks.UserMockUtil;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class PasswordResetTest {

  private UUID tokenId;
  private User user;
  private String token;
  private LocalDateTime expirationDate;

  @BeforeEach
  void setUp() {
    tokenId = UUID.randomUUID();
    user =  UserMockUtil.createMockUser();
    token = "test-token";
    expirationDate = LocalDateTime.now().plusSeconds(PasswordResetToken.TOKEN_EXPIRATION_TIME_SECONDS);
  }

  @Test
  void shouldCreatePasswordResetTokenSuccessfully() {
    PasswordResetToken passwordResetToken = PasswordResetToken.of(tokenId, user, token, expirationDate, false);

    assertNotNull(passwordResetToken);
    assertEquals(tokenId, passwordResetToken.getId());
    assertEquals(user, passwordResetToken.getUser());
    assertEquals(token, passwordResetToken.getToken());
    assertEquals(expirationDate, passwordResetToken.getExpirationDate());
    assertFalse(passwordResetToken.isUsed());
  }

  @Test
  void shouldMarkTokenAsUsed() {
    PasswordResetToken passwordResetToken = PasswordResetToken.of(tokenId, user, token, expirationDate, false);

    passwordResetToken.markAsUsed();

    assertTrue(passwordResetToken.isUsed());
  }

  @Test
  void shouldThrowExceptionWhenMarkingExpiredTokenAsUsed() {
    PasswordResetToken expiredToken = PasswordResetToken.of(tokenId, user, token, LocalDateTime.now().minusSeconds(10), false);

    assertThrows(IllegalStateException.class, expiredToken::markAsUsed);
  }

  @Test
  void shouldReturnTrueIfTokenIsExpired() {
    PasswordResetToken expiredToken = PasswordResetToken.of(tokenId, user, token, LocalDateTime.now().minusSeconds(10), false);

    assertTrue(expiredToken.isExpired());
  }

  @Test
  void shouldReturnFalseIfTokenIsNotExpired() {
    PasswordResetToken validToken = PasswordResetToken.of(tokenId, user, token, expirationDate, false);

    assertFalse(validToken.isExpired());
  }

  @Test
  void shouldReturnTrueIfTokenIsValid() {
    PasswordResetToken validToken = PasswordResetToken.of(tokenId, user, token, expirationDate, false);

    assertTrue(validToken.isValid());
  }

  @Test
  void shouldReturnFalseIfTokenIsExpired() {
    PasswordResetToken expiredToken = PasswordResetToken.of(tokenId, user, token, LocalDateTime.now().minusSeconds(10), false);

    assertFalse(expiredToken.isValid());
  }

  @Test
  void shouldReturnFalseIfTokenIsUsed() {
    PasswordResetToken usedToken = PasswordResetToken.of(tokenId, user, token, expirationDate, true);

    assertFalse(usedToken.isValid());
  }
}
