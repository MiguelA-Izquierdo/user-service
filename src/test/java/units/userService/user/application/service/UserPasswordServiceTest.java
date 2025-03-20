package units.userService.user.application.service;


import com.app.userService._shared.infraestructure.exceptions.InvalidPasswordException;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserRepository;

import com.app.userService.user.domain.service.PasswordEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserPasswordServiceTest {
  @Mock
  private PasswordEncryptionService passwordEncryptionService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserPasswordService userPasswordService;

  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = UserMockUtil.createMockUser();
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void updatePassword_ShouldUpdatePassword_WhenCurrentPasswordIsCorrect() {
    String currentPassword = "old_password";
    String newPassword = "new_password";
    String hashedNewPassword = "hashed_new_password";

    when(passwordEncryptionService.matches(currentPassword, mockUser.getPassword())).thenReturn(true);
    when(passwordEncryptionService.encrypt(newPassword)).thenReturn(hashedNewPassword);

    userPasswordService.updatePassword(mockUser, currentPassword, newPassword);

    assertEquals(hashedNewPassword, mockUser.getPassword());
    verify(userRepository, times(1)).save(mockUser);

  }

  @Test
  void updatePassword_ShouldThrowException_WhenCurrentPasswordIsIncorrect() {
    String wrongPassword = "wrong_password";

    when(passwordEncryptionService.matches(wrongPassword, mockUser.getPassword())).thenReturn(false);

    InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
      userPasswordService.updatePassword(mockUser, wrongPassword, "new_password")
    );

    assertEquals("The current password provided is incorrect.", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  void resetPassword_ShouldUpdatePasswordWithoutCheckingOldPassword() {
    String newPassword = "new_password";
    String hashedNewPassword = "hashed_new_password";

    when(passwordEncryptionService.encrypt(newPassword)).thenReturn(hashedNewPassword);

    userPasswordService.resetPassword(mockUser, newPassword);

    assertEquals(hashedNewPassword, mockUser.getPassword());
    verify(userRepository).save(mockUser);
  }

  @Test
  void encryptPassword_ShouldReturnHashedPassword() {
    String password = "plain_password";
    String hashedPassword = "hashed_password";

    when(passwordEncryptionService.encrypt(password)).thenReturn(hashedPassword);

    String result = userPasswordService.encryptPassword(password);

    assertEquals(hashedPassword, result);
  }

  @Test
  void isPasswordValid_ShouldReturnTrue_WhenPasswordMatches() {
    String rawPassword = "test_password";
    String storedPassword = "hashed_password";

    when(passwordEncryptionService.matches(rawPassword, storedPassword)).thenReturn(true);

    boolean result = userPasswordService.isPasswordValid(rawPassword, storedPassword);

    assertTrue(result);
  }

  @Test
  void isPasswordValid_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
    String rawPassword = "test_password";
    String storedPassword = "hashed_password";

    when(passwordEncryptionService.matches(rawPassword, storedPassword)).thenReturn(false);

    boolean result = userPasswordService.isPasswordValid(rawPassword, storedPassword);

    assertFalse(result);
  }
}
