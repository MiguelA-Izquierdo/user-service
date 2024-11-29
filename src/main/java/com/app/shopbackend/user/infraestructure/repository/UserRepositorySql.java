package com.app.shopbackend.user.infraestructure.repository;

import com.app.shopbackend._shared.security.JwtAuthenticationFilter;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.repositories.UserRepository;
import com.app.shopbackend.user.infraestructure.entities.UserEntity;
import com.app.shopbackend.user.infraestructure.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class UserRepositorySql implements UserRepository {
  private static final Logger logger = LoggerFactory.getLogger(UserRepositorySql.class);

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
  public Optional<User> findByEmail(String email) {
    return jpaRepository.findByEmail(email)
      .map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findByIdOrEmail(UUID userId, String email) {
    return jpaRepository.findByIdOrEmail(userId, email)
      .map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findById(UUID id) {
    return jpaRepository.findById(id)
      .map(UserMapper::toDomain);
  }

  @Override
  public List<User> findAll(int page, int size){
    Pageable pageable = PageRequest.of(page, size);
    Page<UserEntity> userPage = jpaRepository.findAll(pageable);

    logger.info("Found {} userPage, userPageSize {}", userPage.getContent(), userPage.getTotalElements());

    return userPage.getContent()
      .stream()
      .map(UserMapper::toDomain)
      .toList();
  }

}
