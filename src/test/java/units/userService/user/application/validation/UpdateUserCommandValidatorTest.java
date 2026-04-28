package units.userService.user.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.application.validation.UpdateUserCommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserCommandValidatorTest {

  private UpdateUserCommandValidator validator;

  @BeforeEach
  void setUp() {
    validator = new UpdateUserCommandValidator();
  }

  private UpdateUserCommand validCommand() {
    UpdateUserCommand.Phone phone = new UpdateUserCommand.Phone("+34", "633633633");
    UpdateUserCommand.IdentityDocument doc = new UpdateUserCommand.IdentityDocument("Passport", "123456789");
    UpdateUserCommand.Address address = new UpdateUserCommand.Address(
      "Main St", "123", "Springfield", "Illinois", "62704", "USA"
    );
    return new UpdateUserCommand(UUID.randomUUID().toString(), "John", "Doe", doc, phone, address);
  }

  @Test
  void validate_validCommand_noException() {
    assertDoesNotThrow(() -> validator.validate(validCommand()));
  }

  @Test
  void validate_noFieldsProvided_throwsValidationError() {
    UpdateUserCommand command = new UpdateUserCommand(
      UUID.randomUUID().toString(), null, null, null, null, null
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("Invalid request"));
  }

  @Test
  void validate_onlyNameProvided_noException() {
    UpdateUserCommand command = new UpdateUserCommand(
      UUID.randomUUID().toString(), "NewName", null, null, null, null
    );

    assertDoesNotThrow(() -> validator.validate(command));
  }

  @Test
  void validate_invalidUserId_throwsValidationError() {
    UpdateUserCommand command = new UpdateUserCommand(
      "not-a-uuid", "John", null, null, null, null
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("User Id"));
  }

  @Test
  void validate_onlyLastNameProvided_noException() {
    UpdateUserCommand command = new UpdateUserCommand(
      UUID.randomUUID().toString(), null, "NewLastName", null, null, null
    );

    assertDoesNotThrow(() -> validator.validate(command));
  }
}