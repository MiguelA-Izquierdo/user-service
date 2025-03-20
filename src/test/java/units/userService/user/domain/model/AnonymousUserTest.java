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

    Assertions.assertNotNull(anonymousUser, "The anonymous user should not be null");
    Assertions.assertEquals("****", anonymousUser.getName(), "The name should be anonymized as '****'");
    Assertions.assertEquals("***", anonymousUser.getLastName(), "The last name should be anonymized as '***'");
    Assertions.assertEquals("anon-" + mockUser.getId().getValue().toString() + "@anon.com", anonymousUser.getEmail(),
      "The email should be anonymized with 'anon-' prefix and the user's ID");
    Assertions.assertEquals("********", anonymousUser.getDocumentType(), "The document type should be anonymized as '********'");
    Assertions.assertEquals("*********", anonymousUser.getDocumentNumber(), "The document number should be anonymized as '*********'");
    Assertions.assertEquals("***", anonymousUser.getCountryCode(), "The country code should be anonymized as '***'");
    Assertions.assertEquals("*********", anonymousUser.getNumber(), "The phone number should be anonymized as '**********'");
    Assertions.assertEquals("*******", anonymousUser.getStreet(), "The street should be anonymized as '*******'");
    Assertions.assertEquals("***", anonymousUser.getStreetNumber(), "The street number should be anonymized as '***'");
    Assertions.assertEquals("***********", anonymousUser.getCity(), "The city should be anonymized as '***********'");
    Assertions.assertEquals("********", anonymousUser.getState(), "The state should be anonymized as '********'");
    Assertions.assertEquals("*****", anonymousUser.getPostalCode(), "The postal code should be anonymized as '*****'");
    Assertions.assertEquals("***", anonymousUser.getCountry(), "The country should be anonymized as '***'");
    Assertions.assertEquals("***********", anonymousUser.getPassword(), "The password should be anonymized as '***********'");
    Assertions.assertEquals(UserStatus.DELETED, anonymousUser.getStatus(), "The user status should be 'DELETED'");
    Assertions.assertTrue(anonymousUser.getRoles().isEmpty(), "The user should have no roles after anonymization");

  }
}

