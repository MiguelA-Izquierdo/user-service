package units.userService._mocks.repositories;

import com.app.userService.user.domain.model.AnonymousUser;
import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.repositories.UserRepository;

import java.util.UUID;

public class UserRepositoryMock implements UserRepository {
  @Override
  public void save(User user) {

  }

  @Override
  public PaginatedResult<User> findAll(int page, int size) {
    return null;
  }

  @Override
  public UserWrapper findByEmail(String email) {
    return null;
  }

  @Override
  public UserWrapper findByIdOrEmail(UUID userId, String email) {
    return null;
  }

  @Override
  public UserWrapper findById(UUID id) {
    return null;
  }

  @Override
  public void anonymize(AnonymousUser anonymousUser) {

  }
}
