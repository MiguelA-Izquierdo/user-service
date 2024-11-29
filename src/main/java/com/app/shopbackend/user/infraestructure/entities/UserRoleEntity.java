package com.app.shopbackend.user.infraestructure.entities;

import com.app.shopbackend.user.domain.model.Role;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
public class UserRoleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;
  public UserRoleEntity(){}

  private UserRoleEntity(UserEntity userEntity, Role role){
    this.user = userEntity;
    this.role = role;
  }
  public static UserRoleEntity of(UserEntity userEntity, Role role){
    return new UserRoleEntity(userEntity, role);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
