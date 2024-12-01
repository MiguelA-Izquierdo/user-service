package com.app.shopbackend.user.infrastructure.api.controller;

import com.app.shopbackend.user.application.bus.command.CreateUserCommand;
import com.app.shopbackend.user.application.bus.command.GrantSuperAdminCommand;
import com.app.shopbackend.user.application.bus.command.UpdatePasswordCommand;
import com.app.shopbackend.user.application.bus.command.UserCommandBus;
import com.app.shopbackend.user.application.bus.query.GetAllUsersQuery;
import com.app.shopbackend.user.application.bus.query.GetUserByIdQuery;
import com.app.shopbackend.user.application.bus.query.UserQueryBus;
import com.app.shopbackend.user.infrastructure.api.dto.UserResponseDTO;
import com.app.shopbackend.user.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
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
  public ResponseEntity<Object> getByAll(@RequestBody GetAllUsersQuery getAllUsersQuery) {

    List<UserResponseDTO> users = queryBus.send(getAllUsersQuery).stream()
      .map(UserResponseDTO::Of)
      .toList();


    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.OK.value());
    response.put("message", "User data retrieved successfully");
    response.put("users", users);

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
