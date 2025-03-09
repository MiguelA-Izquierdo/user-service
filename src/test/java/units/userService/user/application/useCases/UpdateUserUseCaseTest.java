package units.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.application.useCases.UpdateUserUseCase;
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

class UpdateUserUseCaseTest {
  @Mock
  private UserServiceCore userServiceCore;

  @Mock
  private UserActionLogService userActionLogService;

  @InjectMocks
  private UpdateUserUseCase updateUserUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void execute_shouldUpdateUser_whenUserExistsAndIsActive() {
    User user = UserMockUtil.createMockUser();
    UserId userId = user.getId();

    UpdateUserCommand command = UserMockUtil.createUpdateUserCommand(userId.toString());

    UserWrapper userWrapper = UserWrapper.active(user);
    when(userServiceCore.findUserById(UserId.of(command.userId()))).thenReturn(userWrapper);

    updateUserUseCase.execute(command);

    ArgumentCaptor<UpdateUserCommand> commandCaptor = ArgumentCaptor.forClass(UpdateUserCommand.class);
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userServiceCore, times(1)).updateUser(commandCaptor.capture(), userCaptor.capture());

    UpdateUserCommand capturedCommand = commandCaptor.getValue();
    User capturedUser = userCaptor.getValue();
    assert capturedCommand.userId().equals(userId.toString());
    assert capturedUser.equals(user);

    verify(userActionLogService, times(1)).registerUserAction(user, UserAction.UPDATED);
  }


  @Test
  void execute_shouldThrowEntityNotFoundException_whenUserDoesNotExistOrIsInactive() {
    String userId = UUID.randomUUID().toString();
    UpdateUserCommand command = UserMockUtil.createUpdateUserCommand(userId);
    UserWrapper userWrapper =UserWrapper.notFound();
    when(userServiceCore.findUserById(UserId.of(userId))).thenReturn(userWrapper);
    assertThrows(EntityNotFoundException.class, () -> updateUserUseCase.execute(command));
  }
}
