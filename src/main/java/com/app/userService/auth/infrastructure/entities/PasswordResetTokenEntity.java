package com.app.userService.auth.infrastructure.entities;

import com.app.userService.user.infrastructure.entities.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetTokenEntity {

  @Id
  @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private LocalDateTime expirationDate;

  @Column(nullable = false)
  private boolean isUsed;

  public PasswordResetTokenEntity() {}

  public PasswordResetTokenEntity(UUID id, UserEntity user, String token, LocalDateTime expirationDate, boolean isUsed) {
    this.id = id;
    this.user = user;
    this.token = token;
    this.expirationDate = expirationDate;
    this.isUsed = isUsed;
  }

  public UUID getId() {
    return id;
  }

  public UserEntity getUser() {
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
