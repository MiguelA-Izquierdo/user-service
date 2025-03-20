package com.app.userService.user.infrastructure.entities;

import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.infrastructure.serializer.MetadataConverter;
import jakarta.persistence.*;


import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "user_action_log")
public class UserActionLogEntity {

  @Id
  @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserAction userAction;

  @Column(nullable = false)
  private Instant timestamp;

  @Convert(converter = MetadataConverter.class)
  @Column(name = "metadata", length = 10000)
  private Map<String, String> metadata;

  public UserActionLogEntity() {}

  public UserActionLogEntity(UUID id, UserEntity user, UserAction userAction, Instant timestamp, Map<String, String> metadata) {
    this.id = id;
    this.user = user;
    this.userAction = userAction;
    this.timestamp = timestamp != null ? timestamp : Instant.now();
    this.metadata = metadata;
  }

  public UUID getId() {
    return id;
  }

  public UserEntity getUser() {
    return user;
  }

  public UserAction getUserAction() {
    return userAction;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }
}
