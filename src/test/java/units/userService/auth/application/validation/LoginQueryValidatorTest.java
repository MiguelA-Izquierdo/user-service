package units.userService.auth.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.validation.LoginQueryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginQueryValidatorTest {

  private LoginQueryValidator validator;

  @BeforeEach
  void setUp() {
    validator = new LoginQueryValidator();
  }

  @Test
  void validate_validQuery_noException() {
    LoginQuery query = new LoginQuery("user@example.com", "ValidPass1!@");

    assertDoesNotThrow(() -> validator.validate(query));
  }

  @Test
  void validate_invalidEmail_throwsValidationError() {
    LoginQuery query = new LoginQuery("not-an-email", "ValidPass1!@");

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(query));
    assertTrue(error.getErrors().containsKey("User email"));
  }

  @Test
  void validate_nullEmail_throwsValidationError() {
    LoginQuery query = new LoginQuery(null, "ValidPass1!@");

    assertThrows(ValidationError.class, () -> validator.validate(query));
  }

  @Test
  void validate_passwordNotMeetingComplexity_throwsValidationError() {
    LoginQuery query = new LoginQuery("user@example.com", "weakpassword");

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(query));
    assertTrue(error.getErrors().containsKey("Password"));
  }

  @Test
  void validate_bothFieldsInvalid_containsBothErrors() {
    LoginQuery query = new LoginQuery("bad-email", "weak");

    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(query));
    assertEquals(2, error.getErrors().size());
  }
}