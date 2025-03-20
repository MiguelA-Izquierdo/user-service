package units.userService.user.application.service;

import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserRepository;
import com.app.userService.user.domain.repositories.UserRoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import units.userService._mocks.UserMockUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class UserServiceCoreTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private UserRoleRepository userRoleRepository;

  @Mock
  private UserActionLogService userActionLogService;

  @InjectMocks
  private UserServiceCore userServiceCore;

  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void testRegisterUser_Successful() {
    UserWrapper userWrapper = UserWrapper.notFound();
    Mockito.when(userRepository.findByIdOrEmail(mockUser.getId().getValue(), mockUser.getEmail().getEmail())).thenReturn(userWrapper);

    userServiceCore.registerUser(mockUser);

    verify(userRepository, times(1)).save(mockUser);
    verify(userRoleRepository, times(1)).save(mockUser, Role.ROLE_USER);
  }
  @Test
  void testRegisterUser_UserAlreadyExists() {
    UserWrapper userWrapper = UserWrapper.active(mockUser);
    Mockito.when(userRepository.findByIdOrEmail(mockUser.getId().getValue(), mockUser.getEmail().getEmail())).thenReturn(userWrapper);

    assertThrows(UserAlreadyExistsException.class, () -> userServiceCore.registerUser(mockUser));

    verify(userRepository, never()).save(any(User.class));
    verify(userRoleRepository, never()).save(any(User.class), any(Role.class));
  }

  @Test
  void testGrantSuperAdmin_Successful() {
    Mockito.when(userRoleRepository.findRoleByUserIdRoleName(mockUser, Role.ROLE_SUPER_ADMIN)).thenReturn(Optional.empty());

    userServiceCore.grantSuperAdmin(mockUser);

    verify(userRoleRepository, times(1)).save(mockUser, Role.ROLE_SUPER_ADMIN);
  }
  @Test
  void testGrantSuperAdmin_RoleAlreadyGrantedException() {
    Mockito.when(userRoleRepository.findRoleByUserIdRoleName(mockUser, Role.ROLE_SUPER_ADMIN)).thenReturn(Optional.of(Role.ROLE_SUPER_ADMIN));

    assertThrows(RoleAlreadyGrantedException.class, () -> userServiceCore.grantSuperAdmin(mockUser));
    verify(userRoleRepository, never()).save(mockUser, Role.ROLE_SUPER_ADMIN);
  }

  @Test
  void testFindUserById() {
    Mockito.when(userRepository.findById(mockUser.getId().getValue())).thenReturn(UserWrapper.notFound());

    userServiceCore.findUserById(mockUser.getId());

    verify(userRepository, times(1)).findById(mockUser.getId().getValue());
  }

  @Test
  void testFindUserByEmail() {
    Mockito.when(userRepository.findByEmail(mockUser.getEmail().getEmail())).thenReturn(UserWrapper.notFound());

    userServiceCore.findUserByEmail(mockUser.getEmail().getEmail());

    verify(userRepository, times(1)).findByEmail(mockUser.getEmail().getEmail());
  }

  @Test
  void testFindAll_ReturnsPaginatedUsers() {
    int page = 0;
    int size = 2;
    List<User> users = Arrays.asList(mockUser, mockUser);
    PaginatedResult<User> paginatedResult = PaginatedResult.of(users,2,1);

    when(userRepository.findAll(page, size)).thenReturn(paginatedResult);

    PaginatedResult<User> result = userServiceCore.findAll(page, size);

    verify(userRepository, times(1)).findAll(page, size);

    assertNotNull(result);
    assertEquals(2, result.getItems().size());
    assertEquals(mockUser, result.getItems().get(0));
    assertEquals(mockUser, result.getItems().get(1));
    assertEquals(2, result.getTotalItems());
  }
  @Test
  void testFindAll_EmptyResult() {
    int page = 0;
    int size = 2;
    PaginatedResult<User> emptyResult = PaginatedResult.of(List.of(),0, 0);

    when(userRepository.findAll(page, size)).thenReturn(emptyResult);

    PaginatedResult<User> result = userServiceCore.findAll(page, size);

    verify(userRepository, times(1)).findAll(page, size);

    assertNotNull(result);
    assertTrue(result.getItems().isEmpty());
    assertEquals(0, result.getTotalItems());
  }

  @Test
  void testAnonymizeUser() {
    AnonymousUser anonymousUser = AnonymousUser.of(mockUser);

    doNothing().when(userRepository).anonymize(anonymousUser);
    doNothing().when(userRoleRepository).deleteByUser(mockUser);

    userServiceCore.anonymizeUser(mockUser);

    ArgumentCaptor<AnonymousUser> captor = ArgumentCaptor.forClass(AnonymousUser.class);
    Assertions.assertEquals("anon-" + mockUser.getId().getValue().toString() + "@anon.com", anonymousUser.getEmail());
    verify(userRepository, times(1)).anonymize(captor.capture());
    verify(userRoleRepository, times(1)).deleteByUser(mockUser);
  }

  @Test
  void testUpdateUser_ChangedFields() {
    UpdateUserCommand command = UserMockUtil.createUpdateUserCommand(UUID.randomUUID().toString());

    doNothing().when(userRepository).save(any(User.class));

    userServiceCore.updateUser(command, mockUser);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(captor.capture());

    User updatedUser = captor.getValue();
    assertNotNull(updatedUser);
    assertEquals("321", updatedUser.getAddress().getNumber());
  }

  @Test
  void testUpdateUser_AllFieldNull() {
    UpdateUserCommand command = new UpdateUserCommand(
      mockUser.getId().toString(),
      null,
      null,
      null,
      null,
      null
    );


    userServiceCore.updateUser(command, mockUser);


    verify(userRepository, never()).save(any(User.class));
  }
  @Test
  void testUpdateUser_UnchangedFields() {
    UpdateUserCommand command = UserMockUtil.createUpdateUserCommandFromUser(mockUser);

    userServiceCore.updateUser(command, mockUser);

    verify(userRepository, never()).save(any(User.class));
  }



  @Test
  void shouldUnlockLockedUser() {
    User user = mockUser;
    int maxAttempts = 10;
    int attempts = 0;
    for (; attempts < maxAttempts && !user.isLocked(); attempts++) {
      user.registerFailedLoginAttempt();
    }

    assertTrue(user.isLocked(), "User should be locked after " + attempts + " failed attempts");


    userServiceCore.unlockAccount(user);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(captor.capture());

    User savedUser = captor.getValue();
    assertFalse(savedUser.isLocked(), "User should be unlocked when saved to database");
  }

}
