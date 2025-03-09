package com.app.userService.user.infrastructure.api.controller;

import com.app.userService._shared.infraestructure.dto.ErrorResponseDTO;
import com.app.userService._shared.infraestructure.dto.SuccessResponseDTO;

import com.app.userService.user.application.bus.command.*;
import com.app.userService.user.application.bus.query.GetAllUsersQuery;
import com.app.userService.user.application.bus.query.GetUserByIdQuery;
import com.app.userService.user.application.bus.query.UserQueryBus;
import com.app.userService.user.application.dto.PaginatedUsersDTO;

import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;

import com.app.userService.user.infrastructure.api.dto.CreateUserCommandDTO;
import com.app.userService.user.infrastructure.api.dto.UserResponseDTO;
import com.app.userService.user.infrastructure.service.CreateUserCommandFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private final UserCommandBus commandBus;
  private final UserQueryBus queryBus;
  private final CreateUserCommandFactory createUserCommandFactory;
  public UserController(UserCommandBus commandBus,
                        UserQueryBus queryBus,
                        CreateUserCommandFactory createUserCommandFactory) {
    this.commandBus = commandBus;
    this.queryBus = queryBus;
    this.createUserCommandFactory = createUserCommandFactory;
  }

  @Operation(
    summary = "Create a new user",
    description = "Creates a new user by sending the required data in the request body.",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = CreateUserCommandDTO.class)
      )
    ),
    responses = {
      @ApiResponse(
        responseCode = "201",
        description = "User successfully created",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SuccessResponseDTO.class),
          examples = {
            @ExampleObject(name = "userCreated", value = "{\n  \"success\": true,\n  \"status\": 201,\n  \"message\": \"User created successfully\"\n}"),
          }
        )
      ),
      @ApiResponse(responseCode = "400", description = "Invalid request",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )),
      @ApiResponse(responseCode = "409", description = "The user with the same ID or email already exists.",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class),
          examples = {
            @ExampleObject(name = "userConflict", value = "{\n  \"status\": 409,\n  \"message\": \"The user with the same ID or email already exists.\"\n}")
          }
        )),
      @ApiResponse(responseCode = "500", description = "Internal server error",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        ))
    }
  )
  @PostMapping
  public ResponseEntity<Object> create(@RequestBody CreateUserCommandDTO createUserCommandDTO) {
    CreateUserCommand command = createUserCommandFactory.create(createUserCommandDTO);
    commandBus.dispatch(command);
    SuccessResponseDTO response = SuccessResponseDTO.Of(HttpStatus.CREATED.value(),
      "User created successfully");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
    summary = "Get a paginated list of users",
    description = "Retrieves a paginated list of users with the specified page and size parameters.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer token for authentication. Format: 'Bearer {token}'",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
      ),
      @Parameter(
        name = "page",
        description = "Page number for pagination (default is 0)",
        required = false,
        schema = @Schema(type = "integer", defaultValue = "0")
      ),
      @Parameter(
        name = "size",
        description = "Number of users per page (default is 10)",
        required = false,
        schema = @Schema(type = "integer", defaultValue = "10")
      )
    },
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved the list of users",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SuccessResponseDTO.class)
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
    },
    security = {@SecurityRequirement(name = "bearerAuth")}
  )
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

    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.OK.value(),
      "User data retrieved successfully",
      paginatedUsersDTO);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(
    summary = "Get user details by ID",
    description = "Retrieves the details of a user by their unique ID.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer token for authentication. Format: 'Bearer {token}'",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
      ),
      @Parameter(
        name = "userId",
        description = "The unique identifier of the user to retrieve",
        required = true,
        schema = @Schema(type = "string", example = "12345")
      )
    },
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved the user details",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SuccessResponseDTO.class)
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
        responseCode = "404",
        description = "User not found",
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
  @GetMapping("/{userId}")
  @PreAuthorize("@userAuthorizationFilter.hasAccessToUser(authentication, #userId).granted")
  public ResponseEntity<Object> getById(@PathVariable String userId) {
    GetUserByIdQuery getUserByIdQuery = new GetUserByIdQuery(userId);
    Optional<User> userOptional = queryBus.send(getUserByIdQuery);

    User user = userOptional.orElseThrow(() ->
      new EntityNotFoundException("User with ID " + getUserByIdQuery.getId() + " not found")
    );

    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.OK.value(),
      "User data retrieved successfully",
      UserResponseDTO.Of(user));

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @Operation(
    summary = "Delete a user by ID",
    description = "Deletes a user identified by their unique ID. Requires a valid Bearer token for authentication.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer token for authentication. Format: 'Bearer {token}'",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
      ),
      @Parameter(
        name = "userId",
        description = "The unique identifier of the user to delete",
        required = true,
        schema = @Schema(type = "string", example = "12345")
      )
    },
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "User successfully deleted",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SuccessResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Token not valid or missing",
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
        responseCode = "404",
        description = "User not found",
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
    },
    security = {@SecurityRequirement(name = "bearerAuth")}
  )
  @DeleteMapping("/{userId}")
  @PreAuthorize("@userAuthorizationFilter.hasAccessToUser(authentication, #userId).granted")
  public ResponseEntity<Object> delete(@PathVariable String userId) {
    DeleteUserCommand command = new DeleteUserCommand(userId);
    commandBus.dispatch(command);

    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.NO_CONTENT.value(),
      "User deleted successfully"
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping
  @PreAuthorize("@userAuthorizationFilter.hasAccessToUser(authentication, #userId).granted")
  public ResponseEntity<Object> update(@RequestBody UpdateUserCommand command) {
    commandBus.dispatch(command);

    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.NO_CONTENT.value(),
      "User updated successfully"
    );

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

  @Operation(
    summary = "Grant Super Admin role to a user",
    description = "Assigns the 'Super Admin' role to a user identified by their unique ID. Requires a valid Bearer token for authentication and Super Admin privileges.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer token for authentication. Format: 'Bearer {token}'",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
      ),
      @Parameter(
        name = "userId",
        description = "The unique identifier of the user to grant the Super Admin role",
        required = true,
        schema = @Schema(type = "string", example = "12345")
      )
    },
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully granted Super Admin role",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SuccessResponseDTO.class),
          examples = {
            @ExampleObject(name = "superAdminRoleGranted", value = "{\n  \"status\": 200,\n  \"message\": \"Super Admin role granted successfully\"\n}")
          }
        )
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Token not valid or missing",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "403",
        description = "Forbidden - User does not have Super Admin privileges",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponseDTO.class)
        )
      ),
      @ApiResponse(
        responseCode = "404",
        description = "User not found",
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
    },
    security = {@SecurityRequirement(name = "bearerAuth")}
  )
  @PostMapping("/grant-role-super-admin/{userId}")
  @PreAuthorize("@userAuthorizationFilter.hasAccessSuperAdmin(authentication).granted")
  public ResponseEntity<Object> grantSuperAdminRole(@PathVariable String userId) {
    GrantSuperAdminCommand grantSuperAdminCommand = new GrantSuperAdminCommand(userId);
    commandBus.dispatch(grantSuperAdminCommand);

    SuccessResponseDTO response = SuccessResponseDTO.Of(
      HttpStatus.OK.value(),
      "Super Admin role granted successfully"
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


}
