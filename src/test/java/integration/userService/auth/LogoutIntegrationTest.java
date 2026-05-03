package integration.userService.auth;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserServiceCore userServiceCore;

    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private GrantSuperAdminUseCase grantSuperAdminUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SUPER_ADMIN_EMAIL = "superadmin.logout@example.com";
    private static final String SUPER_ADMIN_PASSWORD = "SuperAdmin@Password1";
    private static final String REGULAR_USER_EMAIL = "user.logout@example.com";
    private static final String REGULAR_USER_PASSWORD = "User@Password1";

    private String superAdminId;
    private String regularUserId;
    private String superAdminToken;
    private String regularUserToken;

    @BeforeEach
    void setUp() throws Exception {
        superAdminId = UUID.randomUUID().toString();
        User superAdmin = User.of(
                UserId.of(superAdminId),
                UserName.of("SuperAdmin"),
                UserLastName.of("Logout"),
                UserEmail.of(SUPER_ADMIN_EMAIL),
                IdentityDocument.of("Passport", "SA9876543"),
                Phone.of("+34", "611000010"),
                Address.of("Admin Street", "1", "Admin City", "Admin State", "11111", "ES"),
                userPasswordService.encryptPassword(SUPER_ADMIN_PASSWORD),
                0,
                LocalDateTime.now(),
                UserStatus.ACTIVE,
                List.of()
        );
        userServiceCore.registerUser(superAdmin);
        grantSuperAdminUseCase.execute(new GrantSuperAdminCommand(superAdminId));

        regularUserId = UUID.randomUUID().toString();
        User regularUser = User.of(
                UserId.of(regularUserId),
                UserName.of("Regular"),
                UserLastName.of("Logout"),
                UserEmail.of(REGULAR_USER_EMAIL),
                IdentityDocument.of("Passport", "US9876543"),
                Phone.of("+34", "622000010"),
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
    void logout_withValidToken_returns204() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isNoContent());

        assertActionLogged(REGULAR_USER_EMAIL, "LOGOUT");
        assertOutboxEventPersisted("user.logout");
        assertNotNull(assertMessagePublishedToQueue("userLoggedQueue"));
    }

    @Test
    void logout_tokenIsInvalidatedAfterLogout_returns401() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/auth")
                        .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutSpecificUser_asSuperAdmin_returns204() throws Exception {
        mockMvc.perform(post("/auth/logout/" + regularUserId)
                        .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void logoutSpecificUser_asRegularUser_returns403() throws Exception {
        mockMvc.perform(post("/auth/logout/" + superAdminId)
                        .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isForbidden());
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
}