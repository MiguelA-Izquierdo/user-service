package units.userService.user.domain.model;

import units.userService._mocks.UserMockUtil;
import com.app.userService.user.domain.model.AnonymousUser;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnonymousUserTest {

  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = UserMockUtil.createMockUser();
  }

  @Test
  void testAnonymousUserCreation() {
    AnonymousUser anonymousUser = AnonymousUser.of(mockUser);

    Assertions.assertNotNull(anonymousUser);
    Assertions.assertEquals("****", anonymousUser.getName());
    Assertions.assertEquals("***", anonymousUser.getLastName());
    Assertions.assertEquals("anon-" + mockUser.getId().getValue().toString() + "@anon.com", anonymousUser.getEmail());
    Assertions.assertEquals("********", anonymousUser.getDocumentType());
    Assertions.assertEquals("*********", anonymousUser.getDocumentNumber());
    Assertions.assertEquals("**", anonymousUser.getCountryCode());
    Assertions.assertEquals("**********", anonymousUser.getNumber());
    Assertions.assertEquals("*******", anonymousUser.getStreet());
    Assertions.assertEquals("***", anonymousUser.getStreetNumber());
    Assertions.assertEquals("***********", anonymousUser.getCity());
    Assertions.assertEquals("********", anonymousUser.getState());
    Assertions.assertEquals("*****", anonymousUser.getPostalCode());
    Assertions.assertEquals("***", anonymousUser.getCountry());
    Assertions.assertEquals("***********", anonymousUser.getPassword());
    Assertions.assertEquals(UserStatus.DELETED, anonymousUser.getStatus());
    Assertions.assertTrue(anonymousUser.getRoles().isEmpty());
  }
}

