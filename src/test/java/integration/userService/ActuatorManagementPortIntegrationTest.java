package integration.userService;

import com.app.userService.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that Actuator is served on a SEPARATE management port, reachable by
 * Kubernetes probes without authentication, while the main application port
 * stays behind the JWT filter. Uses a real server (RANDOM_PORT) because the
 * management-port split is only honoured with an actual servlet container.
 *
 * Reuses the singleton MySQL/RabbitMQ containers started by {@link IntegrationTestBase}.
 */
@SpringBootTest(
    classes = UserService.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "management.server.port=0" // random, isolated management port for the test
    }
)
@ActiveProfiles("test")
class ActuatorManagementPortIntegrationTest {

    @LocalServerPort
    int serverPort;

    @LocalManagementPort
    int managementPort;

    @Autowired
    TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Boot the shared singleton containers from IntegrationTestBase
        registry.add("DB_URL", IntegrationTestBase.mysql::getJdbcUrl);
        registry.add("DB_USER", IntegrationTestBase.mysql::getUsername);
        registry.add("DB_PASSWORD", IntegrationTestBase.mysql::getPassword);

        registry.add("spring.rabbitmq.host", IntegrationTestBase.rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", IntegrationTestBase.rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", IntegrationTestBase.rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", IntegrationTestBase.rabbitmq::getAdminPassword);
    }

    @Test
    void actuatorRunsOnaSeparatePortFromTheApi() {
        assertNotEquals(serverPort, managementPort,
            "Actuator must run on its own management port, isolated from the public API port");
    }

    @Test
    void readinessProbe_onManagementPort_returns200_withoutToken() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + managementPort + "/actuator/health/readiness", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
            "Readiness probe must return 200 without authentication (it is not behind the JWT filter)");
        assertTrue(response.getBody() != null && response.getBody().contains("UP"));
    }

    @Test
    void livenessProbe_onManagementPort_returns200_withoutToken() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + managementPort + "/actuator/health/liveness", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().contains("UP"));
    }

    @Test
    void apiPort_withoutToken_isStillProtected() {
        // Sanity check: the main port must NOT be open just because management is.
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + serverPort + "/users", String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
            "The application port must remain behind the JWT filter");
    }
}