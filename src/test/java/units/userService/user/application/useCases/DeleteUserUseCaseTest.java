package units.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.DeleteUserCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.application.useCases.DeleteUserUseCase;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteUserUseCaseTest {

  @Mock
  private UserServiceCore userServiceCore;

  @Mock
  private UserEventService userEventService;

  @Mock
  private UserActionLogService userActionLogService;

  @InjectMocks
  private DeleteUserUseCase deleteUserUseCase;

  private DeleteUserCommand deleteUserCommand;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    deleteUserCommand = new DeleteUserCommand(UUID.randomUUID().toString());
  }

  @Test
  void execute_shouldDeleteUserAndTriggerActions() {
    User user = mock(User.class);
    UserWrapper userWrapper = mock(UserWrapper.class);
    when(userWrapper.exists()).thenReturn(true);
    when(userWrapper.isActive()).thenReturn(true);
    when(userWrapper.getUser()).thenReturn(java.util.Optional.of(user));

    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(userWrapper);

    doNothing().when(userServiceCore).anonymizeUser(any(User.class));

    deleteUserUseCase.execute(deleteUserCommand);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userServiceCore, times(1)).anonymizeUser(userCaptor.capture());
    verify(userActionLogService, times(1)).registerUserAction(userCaptor.capture(), eq(UserAction.DELETED));
    verify(userEventService, times(1)).handleUserDeletedEvent(userCaptor.capture());

    assert userCaptor.getValue() == user;

    verify(userServiceCore, times(1)).findUserById(any(UserId.class));
  }

  @Test
  void execute_shouldThrowEntityNotFoundException_whenUserDoesNotExist() {
    UserWrapper userWrapper = mock(UserWrapper.class);
    when(userWrapper.exists()).thenReturn(false);
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(userWrapper);

    assertThrows(EntityNotFoundException.class, () -> deleteUserUseCase.execute(deleteUserCommand));
  }

  @Test
  void execute_shouldThrowEntityNotFoundException_whenUserIsInactive() {
    UserWrapper userWrapper = mock(UserWrapper.class);
    when(userWrapper.exists()).thenReturn(true);
    when(userWrapper.isActive()).thenReturn(false);
    when(userServiceCore.findUserById(any(UserId.class))).thenReturn(userWrapper);

    assertThrows(EntityNotFoundException.class, () -> deleteUserUseCase.execute(deleteUserCommand));
  }
}
