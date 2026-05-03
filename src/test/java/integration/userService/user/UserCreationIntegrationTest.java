package integration.userService.user;

import com.app.userService.user.application.bus.command.GrantSuperAdminCommand;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.application.useCases.GrantSuperAdminUseCase;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.valueObjects.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.userService.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserCreationIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserServiceCore userServiceCore;

    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private GrantSuperAdminUseCase grantSuperAdminUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SUPER_ADMIN_EMAIL = "superadmin.creation@example.com";
    private static final String SUPER_ADMIN_PASSWORD = "SuperAdmin@Password1";
    private static final String REGULAR_USER_EMAIL = "user.creation@example.com";
    private static final String REGULAR_USER_PASSWORD = "User@Password1";

    private String superAdminToken;
    private String regularUserToken;

    @BeforeEach
    void setUp() throws Exception {
        String superAdminId = UUID.randomUUID().toString();
        User superAdmin = User.of(
                UserId.of(superAdminId),
                UserName.of("SuperAdmin"),
                UserLastName.of("Test"),
                UserEmail.of(SUPER_ADMIN_EMAIL),
                IdentityDocument.of("Passport", "SA1234567"),
                Phone.of("+34", "611000001"),
                Address.of("Admin Street", "1", "Admin City", "Admin State", "11111", "ES"),
                userPasswordService.encryptPassword(SUPER_ADMIN_PASSWORD),
                0,
                LocalDateTime.now(),
                UserStatus.ACTIVE,
                List.of()
        );
        userServiceCore.registerUser(superAdmin);
        grantSuperAdminUseCase.execute(new GrantSuperAdminCommand(superAdminId));

        User regularUser = User.of(
                UserId.of(UUID.randomUUID().toString()),
                UserName.of("Regular"),
                UserLastName.of("User"),
                UserEmail.of(REGULAR_USER_EMAIL),
                IdentityDocument.of("Passport", "US7654321"),
                Phone.of("+34", "622000002"),
                Address.of("User Street", "2", "User City", "User State", "22222", "ES"),
                userPasswordService.encryptPassword(REGULAR_USER_PASSWORD),
                0,
                LocalDateTime.now(),
                UserStatus.ACTIVE,
                List.of()
        );
        userServiceCore.registerUser(regularUser);

        superAdminToken = loginAndExtractToken(SUPER_ADMIN_EMAIL, SUPER_ADMIN_PASSWORD);
        regularUserToken = loginAndExtractToken(REGULAR_USER_EMAIL, REGULAR_USER_PASSWORD);
        flushOutboxFromSetup();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void createUser_asSuperAdmin_returns201() throws Exception {
        String newUserEmail = "newuser@example.com";

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + superAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload(UUID.randomUUID().toString(), newUserEmail)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("User created successfully"));

        assertUserPersistedInDb(newUserEmail);
        assertActionLogged(newUserEmail, "CREATED");
        assertOutboxEventPersisted("user.created");
        assertNotNull(assertMessagePublishedToQueue("userCreatedQueue"));
    }

    @Test
    void createUser_withDuplicateEmail_returns409() throws Exception {
        String email = "duplicate@example.com";
        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + superAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload(UUID.randomUUID().toString(), email)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + superAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload(UUID.randomUUID().toString(), email)))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_asRegularUser_returns403() throws Exception {
        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + regularUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload(UUID.randomUUID().toString(), "forbidden@example.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload(UUID.randomUUID().toString(), "noauth@example.com")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createUser_withInvalidFields_returns400() throws Exception {
        String invalidPayload = """
                {
                  "id": "%s",
                  "name": "",
                  "lastName": "User",
                  "email": "not-a-valid-email",
                  "password": "weak",
                  "countryCode": "+34",
                  "number": "600000000",
                  "documentType": "Passport",
                  "documentNumber": "AB1234567",
                  "street": "Test Street",
                  "streetNumber": "1",
                  "city": "Test City",
                  "state": "Test State",
                  "postalCode": "12345",
                  "country": "ES"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + superAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    private String loginAndExtractToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("token").asText();
    }

    private String validPayload(String id, String email) {
        return """
                {
                  "id": "%s",
                  "name": "Test",
                  "lastName": "User",
                  "email": "%s",
                  "password": "Test@Password1",
                  "countryCode": "+34",
                  "number": "600000000",
                  "documentType": "Passport",
                  "documentNumber": "AB1234567",
                  "street": "Test Street",
                  "streetNumber": "1",
                  "city": "Test City",
                  "state": "Test State",
                  "postalCode": "12345",
                  "country": "ES"
                }
                """.formatted(id, email);
    }
}