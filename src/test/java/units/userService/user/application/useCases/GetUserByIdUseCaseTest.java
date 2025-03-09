package units.userService.user.application.useCases;

import com.app.userService.user.application.bus.query.GetUserByIdQuery;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.application.useCases.GetUserByIdUseCase;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetUserByIdUseCaseTest {

  @Mock
  private UserServiceCore userServiceCore;

  @InjectMocks
  private GetUserByIdUseCase getUserByIdUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void execute_shouldReturnUser_whenUserExists() {
    String userId = UUID.randomUUID().toString();
    GetUserByIdQuery query = new GetUserByIdQuery(userId);
    User mockUser = UserMockUtil.createMockUser();
    UserWrapper userWrapper = UserWrapper.active(mockUser);

    when(userServiceCore.findUserById(ArgumentMatchers.any(UserId.class)))
      .thenReturn(userWrapper);

    Optional<User> result = getUserByIdUseCase.execute(query);

    assertTrue(result.isPresent());

    assertEquals(mockUser.getId().getValue(), result.get().getId().getValue());
    assertEquals(mockUser.getName().getValue(), result.get().getName().getValue());
    assertEquals(mockUser.getLastName().getValue(), result.get().getLastName().getValue());
    assertEquals(mockUser.getEmail().toString(), result.get().getEmail().toString());
    verify(userServiceCore, Mockito.times(1)).findUserById(ArgumentMatchers.any(UserId.class));
  }

  @Test
  void execute_shouldReturnEmpty_whenUserDoesNotExist() {
    String userId = UUID.randomUUID().toString();
    GetUserByIdQuery query = new GetUserByIdQuery(userId);
    UserWrapper userWrapper = UserWrapper.inactive();
    when(userServiceCore.findUserById(ArgumentMatchers.any(UserId.class)))
      .thenReturn(userWrapper);

    Optional<User> result = getUserByIdUseCase.execute(query);

    assertTrue(result.isEmpty());
    verify(userServiceCore, Mockito.times(1)).findUserById(ArgumentMatchers.any(UserId.class));
  }

}
