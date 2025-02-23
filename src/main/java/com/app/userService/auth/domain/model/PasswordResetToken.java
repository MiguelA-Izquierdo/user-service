package com.app.userService.auth.domain.model;

import com.app.userService.user.domain.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetToken {
  public static final long TOKEN_EXPIRATION_TIME_SECONDS = 3600;


  private final UUID id;
  private final User user;
  private final String token;
  private final LocalDateTime expirationDate;
  private boolean isUsed;

  private PasswordResetToken(UUID id, User user, String token, LocalDateTime expirationDate, boolean isUsed) {
    this.id = id;
    this.user = user;
    this.token = token;
    this.expirationDate = expirationDate;
    this.isUsed = isUsed;
  }

  public static PasswordResetToken of(UUID tokenId, User user, String token, LocalDateTime expirationDate, boolean isUsed) {
    return new PasswordResetToken(tokenId, user, token, expirationDate, isUsed);
  }

  public void markAsUsed() {
    if (this.isExpired()) {
      throw new IllegalStateException("Cannot mark expired token as used.");
    }
    this.isUsed = true;
  }

  public boolean isExpired() {
    return this.expirationDate.isBefore(LocalDateTime.now());
  }

  public boolean isValid(){
    return !this.isExpired() && !this.isUsed();
  }
  public UUID getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getToken() {
    return token;
  }

  public LocalDateTime getExpirationDate() {
    return expirationDate;
  }

  public boolean isUsed() {
    return isUsed;
  }
}
