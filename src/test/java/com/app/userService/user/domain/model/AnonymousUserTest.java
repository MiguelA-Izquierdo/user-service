package com.app.userService.user.domain.model;

import com.app.userService.user.domain.valueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class AnonymousUserTest {

  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = Mockito.mock(User.class);

    // Mock UserId
    UserId mockUserId = Mockito.mock(UserId.class);
    Mockito.when(mockUserId.getValue()).thenReturn(UUID.randomUUID());
    Mockito.when(mockUser.getId()).thenReturn(mockUserId);

    // Mock Name and LastName
    UserName mockName = Mockito.mock(UserName.class);
    Mockito.when(mockName.getValue()).thenReturn("John");
    Mockito.when(mockUser.getName()).thenReturn(mockName);

    UserLastName mockLastName = Mockito.mock(UserLastName.class);
    Mockito.when(mockLastName.getValue()).thenReturn("Doe");
    Mockito.when(mockUser.getLastName()).thenReturn(mockLastName);

    // Mock IdentityDocument
    IdentityDocument mockIdentityDocument = Mockito.mock(IdentityDocument.class);
    Mockito.when(mockIdentityDocument.getDocumentType()).thenReturn("Passport");
    Mockito.when(mockIdentityDocument.getDocumentNumber()).thenReturn("123456789");
    Mockito.when(mockUser.getIdentityDocument()).thenReturn(mockIdentityDocument);

    // Mock Phone
    Phone mockPhone = Mockito.mock(Phone.class);
    Mockito.when(mockPhone.getCountryCode()).thenReturn("+1");
    Mockito.when(mockPhone.getNumber()).thenReturn("1234567890");
    Mockito.when(mockUser.getPhone()).thenReturn(mockPhone);

    // Mock Address
    Address mockAddress = Mockito.mock(Address.class);
    Mockito.when(mockAddress.getStreet()).thenReturn("Main St");
    Mockito.when(mockAddress.getNumber()).thenReturn("123");
    Mockito.when(mockAddress.getCity()).thenReturn("Springfield");
    Mockito.when(mockAddress.getState()).thenReturn("Illinois");
    Mockito.when(mockAddress.getPostalCode()).thenReturn("62704");
    Mockito.when(mockAddress.getCountry()).thenReturn("USA");
    Mockito.when(mockUser.getAddress()).thenReturn(mockAddress);

    // Mock Password
    Mockito.when(mockUser.getPassword()).thenReturn("password123");

    // Mock CreatedAt
    Mockito.when(mockUser.getCreatedAt()).thenReturn(LocalDateTime.now());
  }

  @Test
  void testAnonymousUserCreation() {
    AnonymousUser anonymousUser = AnonymousUser.of(mockUser);

    assertNotNull(anonymousUser);
    assertEquals("****", anonymousUser.getName());
    assertEquals("***", anonymousUser.getLastName());
    assertEquals("anon-" + mockUser.getId().getValue().toString() + "@anon.com", anonymousUser.getEmail());
    assertEquals("********", anonymousUser.getDocumentType());
    assertEquals("*********", anonymousUser.getDocumentNumber());
    assertEquals("**", anonymousUser.getCountryCode());
    assertEquals("**********", anonymousUser.getNumber());
    assertEquals("*******", anonymousUser.getStreet());
    assertEquals("***", anonymousUser.getStreetNumber());
    assertEquals("***********", anonymousUser.getCity());
    assertEquals("********", anonymousUser.getState());
    assertEquals("*****", anonymousUser.getPostalCode());
    assertEquals("***", anonymousUser.getCountry());
    assertEquals("***********", anonymousUser.getPassword());
    assertEquals(UserStatus.DELETED, anonymousUser.getStatus());
    assertTrue(anonymousUser.getRoles().isEmpty());
  }
}

