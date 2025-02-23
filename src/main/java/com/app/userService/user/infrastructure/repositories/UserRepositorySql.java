package com.app.userService.user.infrastructure.repositories;

import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserRepository;
import com.app.userService.user.infrastructure.entities.UserEntity;
import com.app.userService.user.infrastructure.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class UserRepositorySql implements UserRepository {
  private static final Logger logger = LoggerFactory.getLogger(UserRepositorySql.class);
  private static final Set<UserStatus> activeStatuses = new HashSet<>() {{
    add(UserStatus.ACTIVE);
    add(UserStatus.LOCKED);
  }};
  private final UserJpaRepository jpaRepository;
  public UserRepositorySql(UserJpaRepository jpaRepository){
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(User user) {
    UserEntity userEntity = UserMapper.toEntity(user);
    jpaRepository.save(userEntity);
  }
  @Override
  public void anonymize(AnonymousUser anonymousUser){
    UserEntity userEntity = UserMapper.toEntity(anonymousUser);
    jpaRepository.save(userEntity);
  }

  @Override
  public UserWrapper findByEmail(String email) {
    return jpaRepository.findByEmail(email)
      .map(userEntity -> {
        if (isActiveStatus(userEntity.getStatus())){
          User user = UserMapper.toDomain(userEntity);
          return UserWrapper.active(user);
        } else {
          return UserWrapper.inactive();
        }
      })
      .orElse(UserWrapper.notFound());
  }

  @Override
  public UserWrapper findByIdOrEmail(UUID userId, String email) {
    return jpaRepository.findByIdOrEmail(userId, email)
      .map(userEntity -> {
        if (isActiveStatus(userEntity.getStatus())) {
          User user = UserMapper.toDomain(userEntity);
          return UserWrapper.active(user);
        } else {
          return UserWrapper.inactive();
        }
      })
      .orElse(UserWrapper.notFound());
  }

  @Override
  public UserWrapper findById(UUID id) {
    return jpaRepository.findById(id)
      .map(userEntity -> {
        if (isActiveStatus(userEntity.getStatus())) {
          User user = UserMapper.toDomain(userEntity);
          return UserWrapper.active(user);
        } else {
          return UserWrapper.inactive();
        }
      })
      .orElse(UserWrapper.notFound());
  }

  @Override
  public PaginatedResult<User> findAll(int page, int size){
    Pageable pageable = PageRequest.of(page, size);
    Page<UserEntity> userPage = jpaRepository.findAllByStatusIn(activeStatuses,pageable);

    List<User> users = userPage.getContent()
      .stream()
      .map(UserMapper::toDomain)
      .toList();
    long totalUsers = userPage.getTotalElements();
    int totalPages =  userPage.getTotalPages();


    return PaginatedResult.of(users, totalUsers, totalPages);
  }

  private boolean isActiveStatus(UserStatus status) {
    return activeStatuses.contains(status);
  }
}
