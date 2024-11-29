package com.app.shopbackend.auth.domain.valueObjects;


import java.util.Date;

public record AuthToken(String token, Date expirationDate) {
}
