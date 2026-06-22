package integration.userService.user;

import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdatePasswordIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserServiceCore userServiceCore;

    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_A_EMAIL = "usera.password@example.com";
    private static final String USER_A_PASSWORD = "UserA@Password1";
    private static final String USER_B_EMAIL = "userb.password@example.com";
    private static final String USER_B_PASSWORD = "UserB@Password1";
    private static final String NEW_PASSWORD = "NewPass@Password1";

    private String userAId;
    private String userBId;
    private String userAToken;
    private String userBToken;

    @BeforeEach
    void setUp() throws Exception {
        userAId = UUID.randomUUID().toString();
        userServiceCore.registerUser(User.builder()
                .id(UserId.of(userAId))
                .name(UserName.of("UserA"))
                .lastName(UserLastName.of("Password"))
                .email(UserEmail.of(USER_A_EMAIL))
                .identityDocument(IdentityDocument.of("Passport", "UA1234567"))
                .phone(Phone.of("+34", "666000001"))
                .address(Address.of("A Street", "1", "A City", "A State", "55555", "ES"))
                .password(userPasswordService.encryptPassword(USER_A_PASSWORD))
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .roles(List.of())
                .build());

        userBId = UUID.randomUUID().toString();
        userServiceCore.registerUser(User.builder()
                .id(UserId.of(userBId))
                .name(UserName.of("UserB"))
                .lastName(UserLastName.of("Password"))
                .email(UserEmail.of(USER_B_EMAIL))
                .identityDocument(IdentityDocument.of("Passport", "UB1234567"))
                .phone(Phone.of("+34", "666000002"))
                .address(Address.of("B Street", "2", "B City", "B State", "66666", "ES"))
                .password(userPasswordService.encryptPassword(USER_B_PASSWORD))
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .roles(List.of())
                .build());

        userAToken = loginAndExtractToken(USER_A_EMAIL, USER_A_PASSWORD);
        userBToken = loginAndExtractToken(USER_B_EMAIL, USER_B_PASSWORD);
        flushOutboxFromSetup();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void updatePassword_withValidCurrentPassword_returns200() throws Exception {
        mockMvc.perform(patch("/users/password")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordBody(userAId, USER_A_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Password updated successfully"));

        assertActionLogged(USER_A_EMAIL, "UPDATE_PASSWORD");
    }

    @Test
    void updatePassword_afterChange_canLoginWithNewPassword() throws Exception {
        mockMvc.perform(patch("/users/password")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordBody(userAId, USER_A_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(USER_A_EMAIL, NEW_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassword_withWrongCurrentPassword_returns401() throws Exception {
        mockMvc.perform(patch("/users/password")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordBody(userAId, "WrongPass@99", NEW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePassword_forAnotherUser_returns403() throws Exception {
        mockMvc.perform(patch("/users/password")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordBody(userBId, USER_B_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePassword_withoutAuth_returns401() throws Exception {
        mockMvc.perform(patch("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordBody(userAId, USER_A_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    private String updatePasswordBody(String id, String currentPassword, String newPassword) {
        return """
                {"id": "%s", "currentPassword": "%s", "newPassword": "%s"}
                """.formatted(id, currentPassword, newPassword);
    }

    private String loginBody(String email, String password) {
        return """
                {"email": "%s", "password": "%s"}
                """.formatted(email, password);
    }

    private String loginAndExtractToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("token").asText();
    }
}