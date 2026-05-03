package integration.userService.auth;

import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.valueObjects.*;
import integration.userService.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserServiceCore userServiceCore;

    @Autowired
    private UserPasswordService userPasswordService;

    private static final String TEST_EMAIL = "test.login@example.com";
    private static final String TEST_PASSWORD = "Test@Password1";

    @BeforeEach
    void setUp() {
        User user = User.of(
                UserId.of(UUID.randomUUID().toString()),
                UserName.of("Test"),
                UserLastName.of("User"),
                UserEmail.of(TEST_EMAIL),
                IdentityDocument.of("Passport", "AB1234567"),
                Phone.of("+34", "600000000"),
                Address.of("Test Street", "1", "Test City", "Test State", "12345", "ES"),
                userPasswordService.encryptPassword(TEST_PASSWORD),
                0,
                LocalDateTime.now(),
                UserStatus.ACTIVE,
                List.of(Role.ROLE_USER)
        );
        userServiceCore.registerUser(user);
        flushOutboxFromSetup();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void login_withValidCredentials_returns200WithToken() throws Exception {
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(TEST_EMAIL, TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty());

        assertActionLogged(TEST_EMAIL, "LOGGED");
        assertOutboxEventPersisted("user.logged");
        assertNotNull(assertMessagePublishedToQueue("userLoggedQueue"));
    }

    @Test
    void login_withWrongPassword_returns4xxError() throws Exception {
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "WrongPassword99@"}
                                """.formatted(TEST_EMAIL)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login_withNonExistentEmail_returns401() throws Exception {
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "nonexistent@example.com", "password": "%s"}
                                """.formatted(TEST_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }
}