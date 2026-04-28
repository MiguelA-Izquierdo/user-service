package com.app.userService._shared.infrastructure.init;

import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.valueObjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Component
@Profile("!test")
public class DataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserServiceCore userServiceCore;
    private final UserPasswordService userPasswordService;
    private final UserEventService userEventService;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_LAST_NAME}")
    private String adminLastName;

    public DataInitializer(UserServiceCore userServiceCore,
                           UserPasswordService userPasswordService,
                           UserEventService userEventService) {
        this.userServiceCore = userServiceCore;
        this.userPasswordService = userPasswordService;
        this.userEventService = userEventService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            seedAdminUser();
        } catch (Exception e) {
            logger.warn("Admin user seeding skipped: {}", e.getMessage());
        }
    }

    private void seedAdminUser() {
        if (userServiceCore.findUserByEmail(adminEmail).exists()) {
            logger.info("Admin user already exists, skipping seed.");
            return;
        }

        User admin = User.of(
                UserId.of(UUID.randomUUID().toString()),
                UserName.of(adminName),
                UserLastName.of(adminLastName),
                UserEmail.of(adminEmail),
                IdentityDocument.of("Passport", "ADM000000"),
                Phone.of("+1", "0000000"),
                Address.of("Admin Street", "1", "Admin City", "Admin State", "00000", "US"),
                userPasswordService.encryptPassword(adminPassword),
                0,
                LocalDateTime.now(),
                UserStatus.ACTIVE,
                new ArrayList<>()
        );

        userServiceCore.registerUser(admin);
        userServiceCore.grantSuperAdmin(admin);
        userEventService.handleUserCreatedEvent(admin);

        logger.info("Admin user seeded with email: {}", adminEmail);
    }
}