package units.userService.user.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.application.validation.CreateUserCommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserCommandValidatorTest {

  private CreateUserCommandValidator validator;

  @BeforeEach
  void setUp() {
    validator = new CreateUserCommandValidator();
  }

  private CreateUserCommand validCommand() {
    return new CreateUserCommand(
      UUID.randomUUID().toString(),
      "John", "Doe",
      "johndoe@example.com",
      "ValidPass1!@",
      "+34", "633633633",
      "Passport", "123456789",
      "Main St", "123", "Springfield", "Illinois", "62704", "USA"
    );
  }

  @Test
  void validate_validCommand_noException() {
    assertDoesNotThrow(() -> validator.validate(validCommand()));
  }

  @Test
  void validate_nullUserId_throwsValidationError() {
    CreateUserCommand command = new CreateUserCommand(
      null, "John", "Doe", "johndoe@example.com", "ValidPass1!@",
      "+34", "633633633", "Passport", "123456789",
      "Main St", "123", "Springfield", "Illinois", "62704", "USA"
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("User Id"));
  }

  @Test
  void validate_invalidEmail_throwsValidationError() {
    CreateUserCommand command = new CreateUserCommand(
      UUID.randomUUID().toString(),
      "John", "Doe", "not-an-email", "ValidPass1!@",
      "+34", "633633633", "Passport", "123456789",
      "Main St", "123", "Springfield", "Illinois", "62704", "USA"
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("Email"));
  }

  @Test
  void validate_invalidPassword_throwsValidationError() {
    CreateUserCommand command = new CreateUserCommand(
      UUID.randomUUID().toString(),
      "John", "Doe", "johndoe@example.com", "weak",
      "+34", "633633633", "Passport", "123456789",
      "Main St", "123", "Springfield", "Illinois", "62704", "USA"
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("Password"));
  }

  @Test
  void validate_nullName_throwsValidationError() {
    CreateUserCommand command = new CreateUserCommand(
      UUID.randomUUID().toString(),
      null, "Doe", "johndoe@example.com", "ValidPass1!@",
      "+34", "633633633", "Passport", "123456789",
      "Main St", "123", "Springfield", "Illinois", "62704", "USA"
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().containsKey("User name"));
  }

  @Test
  void validate_multipleInvalidFields_containsAllErrors() {
    CreateUserCommand command = new CreateUserCommand(
      null, null, null, "not-an-email", "weak",
      "+34", "633633633", "Passport", "123456789",
      "Main St", "123", "Springfield", "Illinois", "62704", "USA"
    );

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(command));
    assertTrue(error.getErrors().size() >= 4);
  }
}