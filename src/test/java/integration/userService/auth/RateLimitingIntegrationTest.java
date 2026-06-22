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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RateLimitingIntegrationTest extends IntegrationTestBase {

    @Autowired private UserServiceCore userServiceCore;
    @Autowired private UserPasswordService userPasswordService;

    private static final String TEST_EMAIL = "ratelimit@example.com";
    private static final String TEST_PASSWORD = "Test@Password1";

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .id(UserId.of(UUID.randomUUID().toString()))
            .name(UserName.of("Rate"))
            .lastName(UserLastName.of("Limit"))
            .email(UserEmail.of(TEST_EMAIL))
            .identityDocument(IdentityDocument.of("Passport", "RL1234567"))
            .phone(Phone.of("+34", "611111111"))
            .address(Address.of("Test Street", "1", "Test City", "Test State", "12345", "ES"))
            .password(userPasswordService.encryptPassword(TEST_PASSWORD))
            .failedLoginAttempts(0)
            .createdAt(LocalDateTime.now())
            .status(UserStatus.ACTIVE)
            .roles(List.of(Role.ROLE_USER))
            .build();
        userServiceCore.registerUser(user);
        flushOutboxFromSetup();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void login_requestsUpToLimit_areAllowed() throws Exception {
        String body = """
            {"email": "%s", "password": "%s"}
            """.formatted(TEST_EMAIL, TEST_PASSWORD);

        for (int i = 0; i < 6; i++) {
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk());
        }
    }

    @Test
    void login_requestBeyondLimit_returns429() throws Exception {
        String body = """
            {"email": "%s", "password": "%s"}
            """.formatted(TEST_EMAIL, TEST_PASSWORD);

        for (int i = 0; i < 6; i++) {
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk());
        }

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.code").value(429))
            .andExpect(jsonPath("$.message").value("Too many requests. Please try again later."));
    }

    @Test
    void unlockResetPassword_requestBeyondLimit_returns429() throws Exception {
        // Body is intentionally invalid — we only care about the rate limit kicking in on request 4
        String body = """
            {"userId": "invalid", "token": "invalid", "newPassword": "NewPass@1"}
            """;

        for (int i = 0; i < 3; i++) {
            final int attempt = i + 1;
            mockMvc.perform(post("/auth/unlock-reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(result ->
                    assertNotEquals(429, result.getResponse().getStatus(),
                        "Request " + attempt + " should not be rate-limited yet"));
        }

        mockMvc.perform(post("/auth/unlock-reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.code").value(429));
    }
}