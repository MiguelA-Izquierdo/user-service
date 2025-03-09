package units.userService.user.application.useCases;

import com.app.userService.user.application.bus.query.GetAllUsersQuery;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.application.useCases.GetAllUsersUseCase;
import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetAllUsersUseCaseTest {

  @Mock
  private UserServiceCore userServiceCore;

  @InjectMocks
  private GetAllUsersUseCase getAllUsersUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  @Test
  void execute_shouldReturnPaginatedUsers() {
    // Arrange
    int page = 1;
    int size = 10;
    GetAllUsersQuery query = new GetAllUsersQuery(page, size);

    User user1 = UserMockUtil.createMockUser();
    User user2 =  UserMockUtil.createMockUser();
    PaginatedResult<User> expectedPaginatedResult = PaginatedResult.of(Arrays.asList(user1, user2), 2, 1);

    Mockito.when(userServiceCore.findAll(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
      .thenReturn(expectedPaginatedResult);

    // Act
    PaginatedResult<User> result = getAllUsersUseCase.execute(query);

    // Assert
    assertEquals(expectedPaginatedResult, result);
    Mockito.verify(userServiceCore, Mockito.times(1))
      .findAll(ArgumentMatchers.eq(page), ArgumentMatchers.eq(size));
  }

}
