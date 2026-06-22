package integration.userService.auth;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountLockIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserServiceCore userServiceCore;

    @Autowired
    private UserPasswordService userPasswordService;

    private static final String USER_EMAIL = "user.lock@example.com";
    private static final String USER_PASSWORD = "User@Password1";
    private static final String WRONG_PASSWORD = "WrongPass@99";
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(UserId.of(UUID.randomUUID().toString()))
                .name(UserName.of("Lock"))
                .lastName(UserLastName.of("Test"))
                .email(UserEmail.of(USER_EMAIL))
                .identityDocument(IdentityDocument.of("Passport", "LK1234567"))
                .phone(Phone.of("+34", "633000001"))
                .address(Address.of("Lock Street", "1", "Lock City", "Lock State", "33333", "ES"))
                .password(userPasswordService.encryptPassword(USER_PASSWORD))
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .roles(List.of())
                .build();
        userServiceCore.registerUser(user);
        flushOutboxFromSetup();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void login_withWrongPassword_beforeLockThreshold_returns401() throws Exception {
        attemptLoginWithWrongPassword(MAX_FAILED_ATTEMPTS - 2);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USER_EMAIL, WRONG_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_onFifthWrongAttempt_locksAccountAndReturns401() throws Exception {
        attemptLoginWithWrongPassword(MAX_FAILED_ATTEMPTS - 1);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USER_EMAIL, WRONG_PASSWORD)))
                .andExpect(status().isUnauthorized());

        assertActionLogged(USER_EMAIL, "LOCKED");
        assertOutboxEventPersisted("user.locked");
        assertNotNull(assertMessagePublishedToQueue("userLockedQueue"));
    }

    @Test
    void login_onFifthWrongAttempt_userStatusIsLocked() throws Exception {
        attemptLoginWithWrongPassword(MAX_FAILED_ATTEMPTS);

        User user = userServiceCore.findUserByEmail(USER_EMAIL)
                .getUser()
                .orElseThrow();

        assertEquals(UserStatus.LOCKED, user.getStatus());
    }

    @Test
    void login_withCorrectPassword_onLockedAccount_returns401() throws Exception {
        attemptLoginWithWrongPassword(MAX_FAILED_ATTEMPTS);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USER_EMAIL, USER_PASSWORD)))
                .andExpect(status().isUnauthorized());
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
}