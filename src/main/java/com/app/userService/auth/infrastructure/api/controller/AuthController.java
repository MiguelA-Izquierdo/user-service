package com.app.userService.auth.infrastructure.api.controller;

import com.app.userService.auth.application.bus.query.AuthQueryBus;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import com.app.userService.auth.infrastructure.api.dto.UserLoggedResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthQueryBus queryBus;
  public AuthController(AuthQueryBus queryBus) {
    this.queryBus = queryBus;
  }
  @PostMapping
  public ResponseEntity<Object> login(@RequestBody LoginQuery query) {
    UserLoggedDTO userLoggedDTO = queryBus.send(query);
    UserLoggedResponseDTO user = new UserLoggedResponseDTO(userLoggedDTO);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("status", HttpStatus.OK.value());
    response.put("message", "User logged successfully");
    response.put("user", user);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


}
