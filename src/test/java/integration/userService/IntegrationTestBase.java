package integration.userService;

import com.app.userService.UserService;
import com.app.userService.user.infrastructure.messaging.outbound.OutboxEventPublisher;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = UserService.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    // Singleton containers: started once for the entire test suite, stopped on JVM exit.
    static final MySQLContainer<?> mysql;
    static final RabbitMQContainer rabbitmq;

    static {
        mysql = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withCommand("mysqld",
                        "--innodb-buffer-pool-size=64M",
                        "--max-connections=20",
                        "--performance-schema=OFF",
                        "--innodb-flush-log-at-trx-commit=0",
                        "--skip-name-resolve"
                );


        rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management-alpine")
                .withEnv("RABBITMQ_VM_MEMORY_HIGH_WATERMARK_ABSOLUTE", "200MiB")
                .waitingFor(
                    Wait.forHttp("/api/aliveness-test/%2F")
                        .forPort(15672)
                        .withBasicCredentials("guest", "guest")
                        .withStartupTimeout(Duration.ofSeconds(120))
                );

        mysql.start();
        rabbitmq.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected OutboxEventPublisher outboxEventPublisher;

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    private static final List<String> ALL_QUEUES = List.of(
            "userCreatedQueue", "userLoggedQueue", "userLockedQueue"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_URL", mysql::getJdbcUrl);
        registry.add("DB_USER", mysql::getUsername);
        registry.add("DB_PASSWORD", mysql::getPassword);

        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
    }

    /**
     * Verifies that a PENDING outbox event of the given type was persisted in the DB.
     * Call this right after the action that should produce the event, before triggering the publisher.
     */
    protected void assertOutboxEventPersisted(String eventType) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM outbox_events WHERE event_type = ? AND status = 'PENDING'",
                Integer.class, eventType);
        assertTrue(count != null && count > 0,
                "Expected a PENDING outbox event of type [" + eventType + "] but found none");
    }

    /**
     * Triggers the outbox publisher and asserts that a message arrives on the given queue.
     * Returns the message for further inspection if needed.
     */
    protected Message assertMessagePublishedToQueue(String queueName) {
        outboxEventPublisher.publishPendingEvents();
        Message msg = rabbitTemplate.receive(queueName, 3000);
        assertNotNull(msg, "Expected a message on queue [" + queueName + "] but received none");
        return msg;
    }

    /**
     * Call at the end of setUp() to discard outbox events and queue messages produced during
     * test setup, so assertions in test methods only see events from the actual test action.
     */
    protected void flushOutboxFromSetup() {
        outboxEventPublisher.publishPendingEvents();
        ALL_QUEUES.forEach(q -> rabbitAdmin.purgeQueue(q, true));
    }

    protected void truncateAllTables() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE user_action_log");
        jdbcTemplate.execute("TRUNCATE TABLE outbox_events");
        jdbcTemplate.execute("TRUNCATE TABLE password_reset_tokens");
        jdbcTemplate.execute("TRUNCATE TABLE user_roles");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        ALL_QUEUES.forEach(q -> rabbitAdmin.purgeQueue(q, true));
    }

    /**
     * Asserts that the given action was logged in user_action_log for the user identified by email.
     * Joins with the users table to avoid binary UUID handling in tests.
     */
    protected void assertActionLogged(String userEmail, String action) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_action_log ual " +
                "JOIN users u ON ual.user_id = u.id " +
                "WHERE ual.user_action = ? AND u.email = ?",
                Integer.class, action, userEmail);
        assertTrue(count != null && count > 0,
                "Expected action [" + action + "] to be logged for [" + userEmail + "] but found none");
    }

    /**
     * Asserts that exactly one user with the given email exists in the users table.
     */
    protected void assertUserPersistedInDb(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class, email);
        assertTrue(count != null && count == 1,
                "Expected user with email [" + email + "] to be persisted in DB but found " + count);
    }
}