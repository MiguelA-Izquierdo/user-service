package units.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.application.useCases.UpdatePasswordUseCase;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import units.userService._mocks.UserMockUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UpdatePasswordUseCaseTest {
  @Mock
  private UserServiceCore userServiceCore;

  @Mock
  private UserPasswordService userPasswordService;

  @Mock
  private UserActionLogService userActionLogService;

  @InjectMocks
  private UpdatePasswordUseCase updatePasswordUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void execute_shouldUpdatePassword_whenUserExistsAndIsActive() {
    User user = UserMockUtil.createMockUser();
    String userId = user.getId().toString();
    UserWrapper userWrapper = UserWrapper.active(user);

    UpdatePasswordCommand command = new UpdatePasswordCommand(userId, "oldPass", "newPass");

    when(userServiceCore.findUserById(UserId.of(command.id()))).thenReturn(userWrapper);
    updatePasswordUseCase.execute(command);
    verify(userPasswordService, times(1)).updatePassword(user, command.currentPassword(), command.newPassword());
    verify(userActionLogService, times(1)).registerUserAction(user, UserAction.UPDATE_PASSWORD);
  }

  @Test
  void execute_shouldThrowEntityNotFoundException_whenUserDoesNotExistOrIsInactive() {
    String userId = UUID.randomUUID().toString();
    UpdatePasswordCommand command = new UpdatePasswordCommand(userId, "oldPass", "newPass");
    UserWrapper userWrapper =UserWrapper.notFound();
    when(userServiceCore.findUserById(UserId.of(userId))).thenReturn(userWrapper);
    assertThrows(EntityNotFoundException.class, () -> updatePasswordUseCase.execute(command));
  }

}
