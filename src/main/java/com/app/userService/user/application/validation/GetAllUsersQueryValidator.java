package com.app.userService.user.application.validation;


import com.app.userService._shared.infraestructure.ValidationError;
import com.app.userService.user.application.bus.query.GetAllUsersQuery;
import org.springframework.stereotype.Component;

@Component
public class GetAllUsersQueryValidator {

  private static final int MAX_PAGE_SIZE = 100;

  public void validate(GetAllUsersQuery query) {
    ValidationError validationError = new ValidationError();

    if (query.page() == null) {
      validationError.addError("page", "Page number cannot be null.");
    } else if (query.page() < 0) {
      validationError.addError("page", "Page number cannot be negative.");
    }

    if (query.size() == null) {
      validationError.addError("size", "Size cannot be null.");
    } else if (query.size() <= 0) {
      validationError.addError("size", "Size must be greater than 0.");
    } else if (query.size() > MAX_PAGE_SIZE) {
      validationError.addError("size", "Size cannot exceed " + MAX_PAGE_SIZE + " items.");
    }

    if (validationError.hasErrors()) {
      throw validationError;
    }
  }


}
