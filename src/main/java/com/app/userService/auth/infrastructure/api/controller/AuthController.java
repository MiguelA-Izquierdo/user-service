package com.app.userService.auth.infrastructure.api.controller;

import com.app.userService._shared.infraestructure.dto.SuccessResponseDTO;
import com.app.userService.auth.application.bus.command.AuthCommandBus;
import com.app.userService.auth.application.bus.command.LogoutUserCommand;
import com.app.userService.auth.application.bus.query.AuthQueryBus;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.bus.query.LoginWithTokenQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthCommandBus commandBus;

  private final AuthQueryBus queryBus;
  public AuthController(AuthQueryBus queryBus, AuthCommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }
  @PostMapping
  public ResponseEntity<Object> login(@RequestBody LoginQuery query) {
    UserLoggedDTO userLoggedDTO = queryBus.send(query);
    SuccessResponseDTO responseDTO = SuccessResponseDTO.Of(
      HttpStatus.OK.value(),
      "User logged successfully",
      userLoggedDTO
    );
    return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
  }

  @GetMapping
  public ResponseEntity<Object> loginWithToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = authentication.getName();

    LoginWithTokenQuery query = new LoginWithTokenQuery(userId);

    UserLoggedDTO userLoggedDTO = queryBus.send(query);
    SuccessResponseDTO responseDTO = SuccessResponseDTO.Of(
      HttpStatus.OK.value(),
      "User logged successfully",
      userLoggedDTO
    );
    return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
  }

  @PostMapping("/logout")
  public ResponseEntity<Object> logout() {
    String executorUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    LogoutUserCommand command = new LogoutUserCommand(executorUserId, executorUserId);

    commandBus.dispatch(command);
    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.NO_CONTENT.value(),
      "User logout successfully"
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/logout/{userIdToLogout}")
  @PreAuthorize("@userAuthorizationFilter.hasAccessToUser(authentication, #userId).granted")
  public ResponseEntity<Object> logoutUser(@PathVariable String userIdToLogout) {
    String executorUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    LogoutUserCommand command = new LogoutUserCommand(userIdToLogout, executorUserId);

    commandBus.dispatch(command);
    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.NO_CONTENT.value(),
      "User logout successfully"
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
