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
    return UserEntity.builder()
      .id(userDomain.getId().getValue())
      .name(userDomain.getName().getValue())
      .lastName(userDomain.getLastName().getValue())
      .email(userDomain.getEmail().getEmail())
      .password(userDomain.getPassword())
      .failedLoginAttempts(userDomain.getFailedLoginAttempts())
      .secretKey(userDomain.getSecretKey())
      .status(userDomain.getStatus())
      .countryCode(userDomain.getPhone().getCountryCode())
      .phoneNumber(userDomain.getPhone().getNumber())
      .documentType(userDomain.getIdentityDocument().getDocumentType())
      .documentNumber(userDomain.getIdentityDocument().getDocumentNumber())
      .street(userDomain.getAddress().getStreet())
      .streetNumber(userDomain.getAddress().getNumber())
      .city(userDomain.getAddress().getCity())
      .state(userDomain.getAddress().getState())
      .postalCode(userDomain.getAddress().getPostalCode())
      .country(userDomain.getAddress().getCountry())
      .createdAt(userDomain.getCreatedAt())
      .build();
  }

  public static UserEntity toEntity(AnonymousUser anonymousUser){
    return UserEntity.builder()
      .id(anonymousUser.getId().getValue())
      .name(anonymousUser.getName())
      .lastName(anonymousUser.getLastName())
      .email(anonymousUser.getEmail())
      .password(anonymousUser.getPassword())
      .failedLoginAttempts(anonymousUser.getFailedLoginAttempts())
      .secretKey(anonymousUser.getSecretKey())
      .status(anonymousUser.getStatus())
      .countryCode(anonymousUser.getCountryCode())
      .phoneNumber(anonymousUser.getNumber())
      .documentType(anonymousUser.getDocumentType())
      .documentNumber(anonymousUser.getDocumentNumber())
      .street(anonymousUser.getStreet())
      .streetNumber(anonymousUser.getStreetNumber())
      .city(anonymousUser.getCity())
      .state(anonymousUser.getState())
      .postalCode(anonymousUser.getPostalCode())
      .country(anonymousUser.getCountry())
      .createdAt(anonymousUser.getCreatedAt())
      .build();
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

    return User.builder()
      .id(UserId.of(userEntity.getId()))
      .name(UserName.of(userEntity.getName()))
      .lastName(UserLastName.of(userEntity.getLastName()))
      .email(UserEmail.of(userEntity.getEmail()))
      .identityDocument(IdentityDocument.of(userEntity.getDocumentType(), userEntity.getDocumentNumber()))
      .phone(Phone.of(userEntity.getCountryCode(), userEntity.getPhoneNumber()))
      .address(userAddress)
      .password(userEntity.getPassword())
      .failedLoginAttempts(userEntity.getFailedLoginAttempts())
      .secretKey(userEntity.getSecretKey())
      .createdAt(userEntity.getCreatedAt())
      .status(userEntity.getStatus())
      .roles(roles)
      .build();
  }
}
