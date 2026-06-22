package com.app.userService.user.domain.model;

import com.app.userService.user.domain.valueObjects.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    this.roles = roles != null ? List.copyOf(roles) : Collections.emptyList();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private UserId id;
    private UserName name;
    private UserLastName lastName;
    private UserEmail email;
    private IdentityDocument identityDocument;
    private Phone phone;
    private Address address;
    private String password;
    private String secretKey;
    private Integer failedLoginAttempts;
    private LocalDateTime createdAt;
    private UserStatus status;
    private List<Role> roles;

    private Builder() {}

    public Builder id(UserId id)                                       { this.id = id; return this; }
    public Builder name(UserName name)                                 { this.name = name; return this; }
    public Builder lastName(UserLastName lastName)                     { this.lastName = lastName; return this; }
    public Builder email(UserEmail email)                              { this.email = email; return this; }
    public Builder identityDocument(IdentityDocument identityDocument) { this.identityDocument = identityDocument; return this; }
    public Builder phone(Phone phone)                                  { this.phone = phone; return this; }
    public Builder address(Address address)                            { this.address = address; return this; }
    public Builder password(String password)                           { this.password = password; return this; }
    public Builder secretKey(String secretKey)                         { this.secretKey = secretKey; return this; }
    public Builder failedLoginAttempts(Integer failedLoginAttempts)    { this.failedLoginAttempts = failedLoginAttempts; return this; }
    public Builder createdAt(LocalDateTime createdAt)                  { this.createdAt = createdAt; return this; }
    public Builder status(UserStatus status)                           { this.status = status; return this; }
    public Builder roles(List<Role> roles)                             { this.roles = roles; return this; }

    public User build() {
      String key = secretKey != null ? secretKey : generateRandomSecretKey();
      return new User(id, name, lastName, email, identityDocument, phone, address,
        password, failedLoginAttempts, key, createdAt, status, roles);
    }
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
    return Collections.unmodifiableList(roles);
  }

  public Integer getFailedLoginAttempts() {
    return failedLoginAttempts;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void logout() {
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

  private static String generateRandomSecretKey() {
    return new SecureRandom()
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
      this.failedLoginAttempts += 1;
    }

    if (this.failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
      this.lockAccount();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User other = (User) obj;
    return Objects.equals(id, other.id)
      && Objects.equals(name, other.name)
      && Objects.equals(lastName, other.lastName)
      && Objects.equals(email, other.email)
      && Objects.equals(identityDocument, other.identityDocument)
      && Objects.equals(phone, other.phone)
      && Objects.equals(address, other.address)
      && Objects.equals(password, other.password)
      && Objects.equals(secretKey, other.secretKey)
      && Objects.equals(failedLoginAttempts, other.failedLoginAttempts)
      && status == other.status
      && Objects.equals(createdAt, other.createdAt)
      && Objects.equals(roles, other.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, lastName, email, identityDocument, phone, address,
      password, secretKey, failedLoginAttempts, status, createdAt, roles);
  }
}