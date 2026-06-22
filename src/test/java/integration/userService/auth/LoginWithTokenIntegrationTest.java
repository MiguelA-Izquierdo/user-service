package integration.userService.auth;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginWithTokenIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserServiceCore userServiceCore;

    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_EMAIL = "user.tokenlogin@example.com";
    private static final String USER_PASSWORD = "User@Password1";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .id(UserId.of(UUID.randomUUID().toString()))
                .name(UserName.of("Token"))
                .lastName(UserLastName.of("Login"))
                .email(UserEmail.of(USER_EMAIL))
                .identityDocument(IdentityDocument.of("Passport", "TL1234567"))
                .phone(Phone.of("+34", "655000001"))
                .address(Address.of("Token Street", "1", "Token City", "Token State", "55555", "ES"))
                .password(userPasswordService.encryptPassword(USER_PASSWORD))
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .roles(List.of())
                .build();
        userServiceCore.registerUser(user);
        userToken = loginAndExtractToken(USER_EMAIL, USER_PASSWORD);
        flushOutboxFromSetup();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void loginWithToken_withValidToken_returns200WithToken() throws Exception {
        mockMvc.perform(get("/auth")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void loginWithToken_returnedTokenIsUsable() throws Exception {
        MvcResult result = mockMvc.perform(get("/auth")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        String newToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("token").asText();

        mockMvc.perform(get("/auth")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk());
    }

    @Test
    void loginWithToken_withInvalidToken_returns401() throws Exception {
        mockMvc.perform(get("/auth")
                        .header("Authorization", "Bearer this.is.not.a.valid.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithToken_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/auth"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithToken_afterLogout_tokenIsInvalidated_returns401() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/auth")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isUnauthorized());
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