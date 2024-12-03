package com.app.userService.auth.domain.valueObjects;


import java.util.Date;

public record AuthToken(String token, Date expirationDate) {
}
