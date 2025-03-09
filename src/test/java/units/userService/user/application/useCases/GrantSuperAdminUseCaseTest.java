package units.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.GrantSuperAdminCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.application.useCases.GrantSuperAdminUseCase;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GrantSuperAdminUseCaseTest {

  @Mock
  private UserServiceCore userServiceCore;

  @Mock
  private UserActionLogService userActionLogService;

  @InjectMocks
  private GrantSuperAdminUseCase grantSuperAdminUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void execute_shouldGrantSuperAdmin_whenUserExistsAndIsActive() {
    String userId = UUID.randomUUID().toString();
    GrantSuperAdminCommand command = new GrantSuperAdminCommand(userId);

    User user = UserMockUtil.createMockUser();

    UserWrapper userWrapper =UserWrapper.active(user);

    when(userServiceCore.findUserById(UserId.of(userId))).thenReturn(userWrapper);

    grantSuperAdminUseCase.execute(command);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userServiceCore, times(1)).grantSuperAdmin(userCaptor.capture());
    User capturedUser = userCaptor.getValue();
    assert capturedUser == user;

    verify(userActionLogService, times(1)).registerUserAction(user, UserAction.GRANTED_ADMIN);
  }

  @Test
  void execute_shouldThrowEntityNotFoundException_whenUserDoesNotExist() {
    String userId = UUID.randomUUID().toString();
    GrantSuperAdminCommand command = new GrantSuperAdminCommand(userId);

    UserWrapper userWrapper = mock(UserWrapper.class);
    when(userWrapper.exists()).thenReturn(false);

    when(userServiceCore.findUserById(UserId.of(userId))).thenReturn(userWrapper);

    assertThrows(EntityNotFoundException.class, () -> {
      grantSuperAdminUseCase.execute(command);
    });
  }

}
