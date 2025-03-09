package units.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.user.application.useCases.CreateUserUseCase;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

class CreateUserUseCaseTest {

  @Mock
  private UserServiceCore userServiceCore;

  @Mock
  private UserPasswordService userPasswordService;

  @Mock
  private UserEventService userEventService;

  @Mock
  private UserActionLogService userActionLogService;

  @InjectMocks
  private CreateUserUseCase createUserUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void execute_shouldCreateUserAndTriggerActions() {
    CreateUserCommand command = UserMockUtil.createCreateUserCommand();

    Mockito.when(userPasswordService.encryptPassword(ArgumentMatchers.anyString())).thenReturn("hashedPassword");

    createUserUseCase.execute(command);


    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userServiceCore).registerUser(userCaptor.capture());
    User createdUser = userCaptor.getValue();

    Mockito.verify(userServiceCore, Mockito.times(1)).registerUser(ArgumentMatchers.any(User.class));

    Mockito.verify(userActionLogService, Mockito.times(1)).registerUserAction(ArgumentMatchers.any(User.class), ArgumentMatchers.eq(UserAction.CREATED));

    Mockito.verify(userEventService, Mockito.times(1)).handleUserCreatedEvent(ArgumentMatchers.any(User.class));

    assert createdUser.getEmail().toString().equals("johndoe@example.com");
    assert createdUser.getPassword().equals("hashedPassword");
  }
}
