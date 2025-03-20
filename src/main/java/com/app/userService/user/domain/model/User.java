package com.app.userService.user.domain.model;

import com.app.userService.user.domain.valueObjects.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class User {
  private static final int MAX_FAILED_ATTEMPTS = 5;
  private final UserId id;
  private final UserName name;
  private final UserLastName lastName;
  private final UserEmail email;
  private final IdentityDocument identityDocument;
  private final Phone phone;
  private final Address address;
  private String password;
  private String secretKey;
  private Integer failedLoginAttempts;
  private UserStatus status;
  private final LocalDateTime createdAt;
  private final List<Role> roles;

  private User(UserId userId,
               UserName userName,
               UserLastName userLastName,
               UserEmail userEmail,
               IdentityDocument identityDocument,
               Phone phone,
               Address address,
               String password,
               Integer failedLoginAttempts,
               String secretKey,
               LocalDateTime createdAt,
               UserStatus status,
               List<Role> roles) {
    this.id = userId;
    this.name = userName;
    this.lastName = userLastName;
    this.email = userEmail;
    this.identityDocument = identityDocument;
    this.phone = phone;
    this.address = address;
    this.createdAt = createdAt;
    this.failedLoginAttempts = failedLoginAttempts;
    this.password = password;
    this.secretKey = secretKey;
    this.status = status;
    this.roles = roles;
  }

  public static User of(UserId userId,
                        UserName userName,
                        UserLastName userLastName,
                        UserEmail userEmail,
                        IdentityDocument identityDocument,
                        Phone phone,
                        Address address,
                        String password,
                        Integer failedLoginAttempts,
                        String secretKey,
                        LocalDateTime createdAt,
                        UserStatus status,
                        List<Role> roles) {
    return new User(userId, userName, userLastName, userEmail, identityDocument, phone, address, password, failedLoginAttempts, secretKey, createdAt, status, roles);
  }

  public static User of(UserId userId,
                        UserName userName,
                        UserLastName userLastName,
                        UserEmail userEmail,
                        IdentityDocument identityDocument,
                        Phone phone,
                        Address address,
                        String password,
                        Integer failedLoginAttempts,
                        LocalDateTime createdAt,
                        UserStatus status,
                        List<Role> roles) {
    String secretKey = generateRandomSecretKey();
    return new User(userId, userName, userLastName, userEmail, identityDocument, phone, address, password, failedLoginAttempts, secretKey, createdAt, status, roles);
  }
  public UserId getId() {
    return id;
  }

  public UserName getName() {
    return name;
  }

  public UserLastName getLastName() {
    return lastName;
  }

  public UserEmail getEmail() {
    return email;
  }

  public IdentityDocument getIdentityDocument() {
    return identityDocument;
  }

  public Phone getPhone() {
    return phone;
  }

  public Address getAddress() {
    return address;
  }

  public String getPassword() {
    return password;
  }

  public UserStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public List<Role> getRoles() {
    return roles;
  }
  public Integer getFailedLoginAttempts() {
    return failedLoginAttempts;
  }
  public String getSecretKey(){
    return secretKey;
  }
  public void logout(){
      this.secretKey = generateRandomSecretKey();
  }
  public void updatePassword(String newPassword) {
    this.secretKey = generateRandomSecretKey();
    this.password = newPassword;
  }
  public boolean isLocked() {
    return this.status == UserStatus.LOCKED;
  }
  private void lockAccount() {
    this.status = UserStatus.LOCKED;
  }
  private static String generateRandomSecretKey(){
    return new Random()
      .ints(18, 48, 122)
      .filter(Character::isLetterOrDigit)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
  }
  public void clearFailedLoginAttempts() {
    this.failedLoginAttempts = 0;
  }
  public void unlockAccount() {
    this.failedLoginAttempts = 0;
    this.status = UserStatus.ACTIVE;
  }
  public void registerFailedLoginAttempt() {
    if (this.failedLoginAttempts == null) {
      this.failedLoginAttempts = 1;
    } else {
      this.failedLoginAttempts +=1;
    }

    if (this.failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
      this.lockAccount();
    }
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    User other = (User) obj;
    Field[] fields = getClass().getDeclaredFields();
    try {
      for (Field field : fields) {
        field.setAccessible(true);
        Object thisValue = field.get(this);
        Object otherValue = field.get(other);

        if (!Objects.equals(thisValue, otherValue)) {
          return false;
        }
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    Field[] fields = getClass().getDeclaredFields();
    try {
      for (Field field : fields) {
        field.setAccessible(true);
        Object value = field.get(this);
        result = 31 * result + (value == null ? 0 : value.hashCode());
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return result;
  }

}
