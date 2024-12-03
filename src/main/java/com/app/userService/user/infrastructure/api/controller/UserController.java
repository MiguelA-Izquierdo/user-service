package com.app.userService.user.infrastructure.api.controller;

import com.app.userService.user.application.bus.command.*;
import com.app.userService.user.application.bus.query.GetAllUsersQuery;
import com.app.userService.user.application.bus.query.GetUserByIdQuery;
import com.app.userService.user.application.bus.query.UserQueryBus;
import com.app.userService.user.application.dto.PaginatedUsersDTO;
import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.infrastructure.api.dto.UserResponseDTO;
import com.app.userService.user.domain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserCommandBus commandBus;
  private final UserQueryBus queryBus;
  public UserController(UserCommandBus commandBus, UserQueryBus queryBus) {
    this.commandBus = commandBus;
    this.queryBus = queryBus;
  }
  @PostMapping
  public ResponseEntity<Object> create(@RequestBody CreateUserCommand command) {
    commandBus.dispatch(command);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.CREATED.value());
    response.put("message", "User created successfully");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  @GetMapping
  @PreAuthorize("@userAuthorizationFilter.hasAccessAdmin(authentication).granted")
  public ResponseEntity<Object> getAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       HttpServletRequest request) {
    GetAllUsersQuery getAllUsersQuery = new GetAllUsersQuery(page, size);
    PaginatedResult<User> paginatedResult = queryBus.send(getAllUsersQuery);
    String baseUrl = String.format("%s://%s:%d%s",
      request.getScheme(),
      request.getServerName(),
      request.getServerPort(),
      request.getRequestURI());

    PaginatedUsersDTO paginatedUsersDTO = PaginatedUsersDTO.of(paginatedResult, page, size, baseUrl);

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.OK.value());
    response.put("message", "User data retrieved successfully");
    response.put("page", paginatedUsersDTO);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
  @GetMapping("/{userId}")
  @PreAuthorize("@userAuthorizationFilter.hasAccessToUser(authentication, #userId).granted")
  public ResponseEntity<Object> getById(@PathVariable String userId) {
    GetUserByIdQuery getUserByIdQuery = new GetUserByIdQuery(userId);
    Optional<User> userOptional = queryBus.send(getUserByIdQuery);

    User user = userOptional.orElseThrow(() ->
      new EntityNotFoundException("User with ID " + getUserByIdQuery.getId() + " not found")
    );

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.OK.value());
    response.put("message", "User data retrieved successfully");
    response.put("user", UserResponseDTO.Of(user));

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
  @DeleteMapping("/{userId}")
  @PreAuthorize("@userAuthorizationFilter.hasAccessToUser(authentication, #userId).granted")
  public ResponseEntity<Object> delete(@PathVariable String userId) {
    DeleteUserCommand command = new DeleteUserCommand(userId);
    commandBus.dispatch(command);

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.OK.value());
    response.put("message", "User deleted successfully");

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
  @PatchMapping("/password")
  public ResponseEntity<Object> updatePassword(@RequestBody UpdatePasswordCommand command) {
    commandBus.dispatch(command);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.OK.value());
    response.put("message", "Password updated successfully");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/grant-role-super-admin/{userId}")
  @PreAuthorize("@userAuthorizationFilter.hasAccessSuperAdmin(authentication).granted")
  public ResponseEntity<Object> grantSuperAdminRole(@PathVariable String userId) {
    GrantSuperAdminCommand grantSuperAdminCommand = new GrantSuperAdminCommand(userId);
    commandBus.dispatch(grantSuperAdminCommand);

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.OK.value());
    response.put("message", "User data retrieved successfully");

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
