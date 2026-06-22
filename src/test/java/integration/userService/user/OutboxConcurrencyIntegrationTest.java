package integration.userService.user;

import integration.userService.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the outbox poller's concurrency guarantee against a real MySQL 8 instance:
 * two instances polling at the same time must claim disjoint sets of rows (SELECT ... FOR
 * UPDATE SKIP LOCKED), so every event is published exactly once. Without the lock both pollers
 * would read the same PENDING rows and double-publish them.
 */
class OutboxConcurrencyIntegrationTest extends IntegrationTestBase {

    private static final int EVENT_COUNT = 30;

    @BeforeEach
    void clean() {
        truncateAllTables();
    }

    @AfterEach
    void tearDown() {
        truncateAllTables();
    }

    @Test
    void concurrentPollers_publishEachEventExactlyOnce() throws Exception {
        for (int i = 0; i < EVENT_COUNT; i++) {
            insertPendingUserCreatedEvent();
        }

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch startGate = new CountDownLatch(1);
        Callable<Void> poll = () -> {
            startGate.await();
            outboxEventPublisher.publishPendingEvents();
            return null;
        };

        Future<Void> first = pool.submit(poll);
        Future<Void> second = pool.submit(poll);
        startGate.countDown();          // release both pollers simultaneously
        first.get(15, TimeUnit.SECONDS);
        second.get(15, TimeUnit.SECONDS);
        pool.shutdown();

        // Drain any rows a poller may have skipped if it lost the race for the whole batch.
        outboxEventPublisher.publishPendingEvents();

        int delivered = drainQueue("userCreatedQueue");
        assertEquals(EVENT_COUNT, delivered, "each event must be delivered exactly once (no duplicates)");

        Integer unfinished = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM outbox_events WHERE status <> 'PROCESSED'", Integer.class);
        assertEquals(0, unfinished, "all events must end up PROCESSED");
    }

    private void insertPendingUserCreatedEvent() {
        jdbcTemplate.update(
                "INSERT INTO outbox_events " +
                "(id, event_type, payload, status, created_at, queue, routing_key, exchange, attempts) " +
                "VALUES (UUID_TO_BIN(?), 'UserCreated', '{\"userId\":\"x\"}', 'PENDING', NOW(6), " +
                "'userCreatedQueue', 'user.created', 'userExchange', 0)",
                UUID.randomUUID().toString());
    }

    private int drainQueue(String queue) {
        int count = 0;
        while (rabbitTemplate.receive(queue, 500) != null) {
            count++;
        }
        return count;
    }
}