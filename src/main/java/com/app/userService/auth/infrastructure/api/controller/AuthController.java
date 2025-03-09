package com.app.userService.auth.infrastructure.api.controller;

import com.app.userService._shared.infraestructure.dto.ErrorResponseDTO;
import com.app.userService._shared.infraestructure.dto.SuccessResponseDTO;

import com.app.userService.auth.application.bus.command.AuthCommandBus;
import com.app.userService.auth.application.bus.command.LogoutUserCommand;
import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.auth.application.bus.query.AuthQueryBus;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.bus.query.LoginWithTokenQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


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
  @Operation(
    summary = "Login a user",
    description = "Authenticates a user in the system by validating their credentials and providing an access token for secure sessions.",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = LoginQuery.class)
      )
    ),
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "User successfully logged in",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = UserLoggedDTO.class)
        )
      ),
      @ApiResponse(responseCode = "400", description = "Invalid request",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )),
      @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(responseCode = "500", description = "Internal server error",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        ))
    }
  )
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
  @Operation(
    summary = "Login a user whith token",
    description = "Authenticates a user in the system by validating his token and providing an access token for secure sessions.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer token for authentication. Format: 'Bearer {token}'",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
      )
    },
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved the user details",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = UserLoggedDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Token not valid",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Forbidden - Access is denied",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      )
    }
  )
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
  @Operation(
    summary = "Logout user",
    description = "Logs out the authenticated user by invalidating their session token.",
    security = { @SecurityRequirement(name = "Bearer Token") },
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "User successfully logged out"
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Unauthorized (invalid or missing token)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error (unexpected issue during logout)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      )
    }
  )
  @PostMapping("/logout")
  public ResponseEntity<Object> logout() {
    String executorUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    LogoutUserCommand command = new LogoutUserCommand(executorUserId, executorUserId);

    commandBus.dispatch(command);
    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.NO_CONTENT.value(),
      "User logout successfully"
    );

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
  }
  @Operation(
    summary = "Logout a specific user",
    description = "Logs out the specified user by invalidating their session token. The user requesting the logout must have valid credentials and proper authorization to perform this action.",
    security = { @SecurityRequirement(name = "Bearer Token") },
    parameters = {
      @Parameter(
        name = "userIdToLogout",
        description = "The ID of the user to be logged out.",
        required = true,
        in = ParameterIn.PATH
      )
    },
    responses = {
      @ApiResponse(
        responseCode = "204",
        description = "User successfully logged out"
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Unauthorized (invalid or missing token, or insufficient privileges)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Forbidden (the user does not have permission to log out the specified user)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "404",
        description = "User not found (specified user ID does not exist)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error (unexpected issue during logout)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      )
    }
  )

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

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
  }
  @Operation(
    summary = "Unlock and reset user password",
    description = "Restores access to a locked user account by using a special reset token. The user must provide a valid token and a new password to unlock the account and reset their credentials.",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = UnlockResetPasswordCommand.class)
      )
    ),
    responses = {
      @ApiResponse(
        responseCode = "204",
        description = "User password successfully unlocked and reset"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid request (missing or incorrect data, or invalid token)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Unauthorized (invalid or expired reset token)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "404",
        description = "User not found (invalid token or user ID)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error (unexpected issue during password reset process)",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      )
    }
  )
  @PostMapping("/unlock-reset-password")
  public ResponseEntity<Object> resetPassword(@RequestBody UnlockResetPasswordCommand command) {
    commandBus.dispatch(command);
    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.NO_CONTENT.value(),
      "User unlocked successfully"
    );

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
  }
}
