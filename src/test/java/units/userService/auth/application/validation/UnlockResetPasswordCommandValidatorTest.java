package units.userService.auth.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.auth.application.validation.UnlockResetPasswordCommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnlockResetPasswordCommandValidatorTest {

  private UnlockResetPasswordCommandValidator validator;

  @BeforeEach
  void setUp() {
    validator = new UnlockResetPasswordCommandValidator();
  }

  @Test
  void validate_validCommand_noException() {
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand("valid-token", "ValidPass1!@");

    assertDoesNotThrow(() -> validator.validate(command));
  }

  @Test
  void validate_nullToken_throwsValidationError() {
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(null, "ValidPass1!@");

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("Token"));
  }

  @Test
  void validate_invalidPassword_throwsValidationError() {
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand("valid-token", "weak");

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("NewPassword"));
  }

  @Test
  void validate_nullTokenAndInvalidPassword_containsBothErrors() {
    UnlockResetPasswordCommand command = new UnlockResetPasswordCommand(null, "weak");

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertEquals(2, error.getErrors().size());
  }
}