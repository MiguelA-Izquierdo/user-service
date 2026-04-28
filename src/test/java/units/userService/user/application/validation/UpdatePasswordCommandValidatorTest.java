package units.userService.user.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.application.validation.UpdatePasswordCommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePasswordCommandValidatorTest {

  private UpdatePasswordCommandValidator validator;

  @BeforeEach
  void setUp() {
    validator = new UpdatePasswordCommandValidator();
  }

  @Test
  void validate_validCommand_noException() {
    UpdatePasswordCommand command = new UpdatePasswordCommand(
      UUID.randomUUID().toString(), "CurrentPass1!@", "NewPass2!@secure"
    );

    assertDoesNotThrow(() -> validator.validate(command));
  }

  @Test
  void validate_nullUserId_throwsValidationError() {
    UpdatePasswordCommand command = new UpdatePasswordCommand(null, "CurrentPass1!@", "NewPass2!@secure");

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("User Id"));
  }

  @Test
  void validate_invalidCurrentPassword_throwsValidationError() {
    UpdatePasswordCommand command = new UpdatePasswordCommand(
      UUID.randomUUID().toString(), "weak", "NewPass2!@secure"
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("Current password"));
  }

  @Test
  void validate_invalidNewPassword_throwsValidationError() {
    UpdatePasswordCommand command = new UpdatePasswordCommand(
      UUID.randomUUID().toString(), "CurrentPass1!@", "weak"
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("New password"));
  }

  @Test
  void validate_allFieldsNull_containsAllErrors() {
    UpdatePasswordCommand command = new UpdatePasswordCommand(null, null, null);

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertEquals(3, error.getErrors().size());
  }
}