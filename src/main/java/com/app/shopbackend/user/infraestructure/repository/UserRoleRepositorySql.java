package com.app.shopbackend.user.infraestructure.repository;

import com.app.shopbackend.user.domain.model.Role;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.repositories.UserRoleRepository;
import com.app.shopbackend.user.infraestructure.entities.UserEntity;
import com.app.shopbackend.user.infraestructure.entities.UserRoleEntity;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;


@Repository
public class UserRoleRepositorySql implements UserRoleRepository {
  @PersistenceContext
  private EntityManager entityManager;
  private final UserRoleJpaRepository jpaRepository;
  public UserRoleRepositorySql(UserRoleJpaRepository jpaRepository){
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(User user, Role role) {
    UserEntity userEntity = entityManager.getReference(UserEntity.class, user.getId().getValue());
    UserRoleEntity userRoleEntity = UserRoleEntity.of(userEntity, role);
    jpaRepository.save(userRoleEntity);
  }

  @Override
  public Optional<Role> findRoleByUserIdRoleName(User user, Role role) {
    List<UserRoleEntity> roles = jpaRepository.findByUserIdRoleName(user.getId().getValue(), role);

    return roles.stream()
      .findFirst()
      .map(UserRoleEntity::getRole);
  }


}

