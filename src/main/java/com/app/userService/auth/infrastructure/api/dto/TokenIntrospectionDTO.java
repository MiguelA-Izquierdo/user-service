package com.app.userService.auth.infrastructure.api.dto;

import java.util.List;

public record TokenIntrospectionDTO(
    String sub,
    List<String> roles,
    boolean isAdmin
) {}