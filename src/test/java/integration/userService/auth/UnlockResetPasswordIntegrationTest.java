package integration.userService.auth;

import com.app.userService.auth.application.service.PasswordResetTokenService;
import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.valueObjects.*;
import integration.userService.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UnlockResetPasswordIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserServiceCore userServiceCore;

    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    private static final String USER_EMAIL = "user.reset@example.com";
    private static final String USER_PASSWORD = "User@Password1";
    private static final String WRONG_PASSWORD = "WrongPass@99";
    private static final String NEW_PASSWORD = "NewPass@Password1";
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .id(UserId.of(UUID.randomUUID().toString()))
                .name(UserName.of("Reset"))
                .lastName(UserLastName.of("Test"))
                .email(UserEmail.of(USER_EMAIL))
                .identityDocument(IdentityDocument.of("Passport", "RT1234567"))
                .phone(Phone.of("+34", "644000001"))
                .address(Address.of("Reset Street", "1", "Reset City", "Reset State", "44444", "ES"))
                .password(userPasswordService.encryptPassword(USER_PASSWORD))
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .roles(List.of())
                .build();
        userServiceCore.registerUser(user);
        lockAccount();
        flushOutboxFromSetup();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void unlockResetPassword_withValidToken_returns204() throws Exception {
        String rawToken = createValidToken();

        mockMvc.perform(post("/auth/unlock-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody(rawToken, NEW_PASSWORD)))
                .andExpect(status().isNoContent());

        assertActionLogged(USER_EMAIL, "UNLOCKED");
    }

    @Test
    void unlockResetPassword_withValidToken_publishesUnlockedEvent() throws Exception {
        String rawToken = createValidToken();

        mockMvc.perform(post("/auth/unlock-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody(rawToken, NEW_PASSWORD)))
                .andExpect(status().isNoContent());

        assertOutboxEventPersisted("user.unlocked");
        assertMessagePublishedToQueue("userUnlockedQueue");
    }

    @Test
    void unlockResetPassword_unlocksAccountAndNewPasswordWorks() throws Exception {
        String rawToken = createValidToken();

        mockMvc.perform(post("/auth/unlock-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody(rawToken, NEW_PASSWORD)))
                .andExpect(status().isNoContent());

        User restoredUser = userServiceCore.findUserByEmail(USER_EMAIL).getUser().orElseThrow();
        assertEquals(UserStatus.ACTIVE, restoredUser.getStatus());

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USER_EMAIL, NEW_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void unlockResetPassword_withExpiredToken_returns401() throws Exception {
        User lockedUser = userServiceCore.findUserByEmail(USER_EMAIL).getUser().orElseThrow();
        String rawToken = randomToken();
        passwordResetTokenService.createPasswordResetToken(
                PasswordResetToken.of(UUID.randomUUID(), lockedUser, rawToken, LocalDateTime.now().minusHours(2), false)
        );

        mockMvc.perform(post("/auth/unlock-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody(rawToken, NEW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unlockResetPassword_withAlreadyUsedToken_returns401() throws Exception {
        User lockedUser = userServiceCore.findUserByEmail(USER_EMAIL).getUser().orElseThrow();
        String rawToken = randomToken();
        passwordResetTokenService.createPasswordResetToken(
                PasswordResetToken.of(UUID.randomUUID(), lockedUser, rawToken, LocalDateTime.now().plusHours(1), true)
        );

        mockMvc.perform(post("/auth/unlock-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody(rawToken, NEW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unlockResetPassword_withUnknownToken_returns404() throws Exception {
        mockMvc.perform(post("/auth/unlock-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody("this-token-does-not-exist-xyz", NEW_PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @Test
    void unlockResetPassword_withWeakNewPassword_returns400() throws Exception {
        String rawToken = createValidToken();

        mockMvc.perform(post("/auth/unlock-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody(rawToken, "weak")))
                .andExpect(status().isBadRequest());
    }

    private String createValidToken() throws Exception {
        User lockedUser = userServiceCore.findUserByEmail(USER_EMAIL).getUser().orElseThrow();
        String rawToken = randomToken();
        passwordResetTokenService.createPasswordResetToken(
                PasswordResetToken.of(UUID.randomUUID(), lockedUser, rawToken, LocalDateTime.now().plusHours(1), false)
        );
        return rawToken;
    }

    private void lockAccount() throws Exception {
        attemptLoginWithWrongPassword(MAX_FAILED_ATTEMPTS);
    }

    private void attemptLoginWithWrongPassword(int times) throws Exception {
        for (int i = 0; i < times; i++) {
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginBody(USER_EMAIL, WRONG_PASSWORD)));
        }
    }

    private String loginBody(String email, String password) {
        return """
                {"email": "%s", "password": "%s"}
                """.formatted(email, password);
    }

    private String resetBody(String token, String newPassword) {
        return """
                {"token": "%s", "newPassword": "%s"}
                """.formatted(token, newPassword);
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}