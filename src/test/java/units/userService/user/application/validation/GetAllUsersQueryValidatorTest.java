package units.userService.user.application.validation;

import com.app.userService._shared.infrastructure.ValidationError;
import com.app.userService.user.application.bus.query.GetAllUsersQuery;
import com.app.userService.user.application.validation.GetAllUsersQueryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetAllUsersQueryValidatorTest {

  private GetAllUsersQueryValidator validator;

  @BeforeEach
  void setUp() {
    validator = new GetAllUsersQueryValidator();
  }

  @Test
  void validate_validQuery_noException() {
    assertDoesNotThrow(() -> validator.validate(new GetAllUsersQuery(0, 10)));
  }

  @Test
  void validate_firstPage_noException() {
    assertDoesNotThrow(() -> validator.validate(new GetAllUsersQuery(0, 1)));
  }

  @Test
  void validate_maxAllowedSize_noException() {
    assertDoesNotThrow(() -> validator.validate(new GetAllUsersQuery(0, 100)));
  }

  @Test
  void validate_nullPage_throwsValidationError() {
    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(new GetAllUsersQuery(null, 10)));
    assertTrue(error.getErrors().containsKey("Pagination"));
  }

  @Test
  void validate_negativePageNumber_throwsValidationError() {
    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(new GetAllUsersQuery(-1, 10)));
    assertTrue(error.getErrors().containsKey("Pagination"));
  }

  @Test
  void validate_nullSize_throwsValidationError() {
    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(new GetAllUsersQuery(0, null)));
    assertTrue(error.getErrors().containsKey("Pagination"));
  }

  @Test
  void validate_zeroSize_throwsValidationError() {
    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(new GetAllUsersQuery(0, 0)));
    assertTrue(error.getErrors().containsKey("Pagination"));
  }

  @Test
  void validate_negativeSize_throwsValidationError() {
    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(new GetAllUsersQuery(0, -1)));
    assertTrue(error.getErrors().containsKey("Pagination"));
  }

  @Test
  void validate_sizeExceedsMax_throwsValidationError() {
    ValidationError error = assertThrows(ValidationError.class, () -> validator.validate(new GetAllUsersQuery(0, 101)));
    assertTrue(error.getErrors().containsKey("Pagination"));
  }
}