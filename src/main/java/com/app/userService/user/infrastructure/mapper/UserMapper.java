package com.app.userService.user.infrastructure.mapper;

import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.AnonymousUser;
import com.app.userService.user.domain.valueObjects.*;
import com.app.userService.user.infrastructure.entities.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

  public static UserEntity toEntity(User userDomain){

    UserEntity userEntity = new UserEntity(
      userDomain.getId().getValue(),
      userDomain.getName().getValue(),
      userDomain.getLastName().getValue(),
      userDomain.getEmail().getEmail(),
      userDomain.getPassword(),
      userDomain.getSecretKey(),
      userDomain.getStatus(),
      userDomain.getPhone().getCountryCode(),
      userDomain.getPhone().getNumber(),
      userDomain.getIdentityDocument().getDocumentType(),
      userDomain.getIdentityDocument().getDocumentNumber(),
      userDomain.getAddress().getStreet(),
      userDomain.getAddress().getNumber(),
      userDomain.getAddress().getCity(),
      userDomain.getAddress().getState(),
      userDomain.getAddress().getPostalCode(),
      userDomain.getAddress().getCountry(),
      userDomain.getCreatedAt()
    );
    return userEntity;
  }

  public static UserEntity toEntity(AnonymousUser anonymousUser){

    UserEntity userEntity = new UserEntity(
      anonymousUser.getId().getValue(),
      anonymousUser.getName(),
      anonymousUser.getLastName(),
      anonymousUser.getEmail(),
      anonymousUser.getPassword(),
      anonymousUser.getSecretKey(),
      anonymousUser.getStatus(),
      anonymousUser.getCountryCode(),
      anonymousUser.getNumber(),
      anonymousUser.getDocumentType(),
      anonymousUser.getDocumentNumber(),
      anonymousUser.getStreet(),
      anonymousUser.getStreetNumber(),
      anonymousUser.getCity(),
      anonymousUser.getState(),
      anonymousUser.getPostalCode(),
      anonymousUser.getCountry(),
      anonymousUser.getCreatedAt()
    );
    return userEntity;
  }

  public static User toDomain(UserEntity userEntity){
    Address userAddress = Address.of(
      userEntity.getStreet(),
      userEntity.getStreetNumber(),
      userEntity.getCity(),
      userEntity.getState(),
      userEntity.getPostalCode(),
      userEntity.getCountry()
    );

    List<Role> roles = userEntity.getRoles().stream()
      .map(userRoleEntity -> Role.valueOf(userRoleEntity.getRole().name()))
      .collect(Collectors.toList());

    User userDomain = User.of(
      UserId.of(userEntity.getId()),
      UserName.of(userEntity.getName()),
      UserLastName.of(userEntity.getLastName()),
      UserEmail.of(userEntity.getEmail()),
      IdentityDocument.of(userEntity.getDocumentType(), userEntity.getDocumentNumber()),
      Phone.of(userEntity.getCountryCode(), userEntity.getPhoneNumber()),
      userAddress,
      userEntity.getPassword(),
      userEntity.getSecretKey(),
      userEntity.getCreatedAt(),
      userEntity.getStatus(),
      roles
    );

    return userDomain;
  }
}
