package units.userService._shared.application.service;

import com.app.userService._shared.application.service.UserEventFactory;
import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.auth.application.service.PasswordResetTokenService;
import com.app.userService.auth.domain.event.UserLockedDomainEvent;
import com.app.userService.auth.domain.event.UserLoggedDomainEvent;
import com.app.userService.auth.domain.event.UserLogoutDomainEvent;
import com.app.userService.auth.domain.service.TokenService;
import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import units.userService._mocks.UserMockUtil;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserEventServiceTest {

  private static final String EXCHANGE = "user.exchange";
  private static final String QUEUE    = "user.test.queue";
  private static final String ROUTING  = "user.test.routing";

  @Mock private OutboxEventRepository outboxEventRepository;
  @Mock private TokenService tokenService;
  @Mock private PasswordResetTokenService passwordResetTokenService;
  @Mock private UserEventFactory userEventFactory;

  private UserEventService userEventService;
  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = UserMockUtil.createMockUser();
    userEventService = new UserEventService(
      outboxEventRepository, new ObjectMapper().registerModule(new JavaTimeModule()),
      tokenService, passwordResetTokenService, userEventFactory);
  }

  // ── handleUserCreatedEvent ────────────────────────────────────────────────

  @Test
  void handleUserCreatedEvent_savesOutboxEventWithPendingStatus() {
    UserCreatedDomainEvent event = userCreatedEvent();
    when(userEventFactory.createUserCreatedEvent(mockUser)).thenReturn(event);

    userEventService.handleUserCreatedEvent(mockUser);

    assertEquals(OutboxEventStatus.PENDING, captureOutboxEvent().getStatus());
  }

  @Test
  void handleUserCreatedEvent_savesOutboxEventWithCorrectRoutingMetadata() {
    UserCreatedDomainEvent event = userCreatedEvent();
    when(userEventFactory.createUserCreatedEvent(mockUser)).thenReturn(event);

    userEventService.handleUserCreatedEvent(mockUser);

    OutboxEvent saved = captureOutboxEvent();
    assertEquals("user.created", saved.getType());
    assertEquals(QUEUE,          saved.getQueue());
    assertEquals(EXCHANGE,       saved.getExchange());
    assertEquals(ROUTING,        saved.getRoutingKey());
  }

  @Test
  void handleUserCreatedEvent_outboxEventIdMatchesDomainEventId() {
    UserCreatedDomainEvent event = userCreatedEvent();
    when(userEventFactory.createUserCreatedEvent(mockUser)).thenReturn(event);

    userEventService.handleUserCreatedEvent(mockUser);

    assertEquals(event.getEventId(), captureOutboxEvent().getEventId());
  }

  // ── handleUserDeletedEvent ────────────────────────────────────────────────

  @Test
  void handleUserDeletedEvent_savesOutboxEventWithPendingStatus() {
    UserDeletedDomainEvent event = userDeletedEvent();
    when(userEventFactory.createUserDeletedEvent(mockUser)).thenReturn(event);

    userEventService.handleUserDeletedEvent(mockUser);

    assertEquals(OutboxEventStatus.PENDING, captureOutboxEvent().getStatus());
  }

  @Test
  void handleUserDeletedEvent_outboxEventIdMatchesDomainEventId() {
    UserDeletedDomainEvent event = userDeletedEvent();
    when(userEventFactory.createUserDeletedEvent(mockUser)).thenReturn(event);

    userEventService.handleUserDeletedEvent(mockUser);

    assertEquals(event.getEventId(), captureOutboxEvent().getEventId());
  }

  // ── handleUserLoggedEvent ─────────────────────────────────────────────────

  @Test
  void handleUserLoggedEvent_savesOutboxEventWithPendingStatus() {
    UserLoggedDomainEvent event = userLoggedEvent();
    when(userEventFactory.createUserLoggedEvent(mockUser, false)).thenReturn(event);

    userEventService.handleUserLoggedEvent(mockUser, false);

    assertEquals(OutboxEventStatus.PENDING, captureOutboxEvent().getStatus());
  }

  @Test
  void handleUserLoggedEvent_outboxEventIdMatchesDomainEventId() {
    UserLoggedDomainEvent event = userLoggedEvent();
    when(userEventFactory.createUserLoggedEvent(mockUser, false)).thenReturn(event);

    userEventService.handleUserLoggedEvent(mockUser, false);

    assertEquals(event.getEventId(), captureOutboxEvent().getEventId());
  }

  // ── handleUserLogoutEvent ─────────────────────────────────────────────────

  @Test
  void handleUserLogoutEvent_savesOutboxEventWithPendingStatus() {
    UserLogoutDomainEvent event = userLogoutEvent();
    when(userEventFactory.createUserLogoutEvent(mockUser)).thenReturn(event);

    userEventService.handleUserLogoutEvent(mockUser);

    assertEquals(OutboxEventStatus.PENDING, captureOutboxEvent().getStatus());
  }

  @Test
  void handleUserLogoutEvent_outboxEventIdMatchesDomainEventId() {
    UserLogoutDomainEvent event = userLogoutEvent();
    when(userEventFactory.createUserLogoutEvent(mockUser)).thenReturn(event);

    userEventService.handleUserLogoutEvent(mockUser);

    assertEquals(event.getEventId(), captureOutboxEvent().getEventId());
  }

  // ── handleUserLockedEvent ─────────────────────────────────────────────────

  @Test
  void handleUserLockedEvent_createsPasswordResetToken() {
    when(tokenService.generateToken()).thenReturn("reset-token");
    when(userEventFactory.createUserLockedEvent(mockUser, "reset-token")).thenReturn(userLockedEvent("reset-token"));

    userEventService.handleUserLockedEvent(mockUser);

    verify(passwordResetTokenService, times(1)).createPasswordResetToken(any());
  }

  @Test
  void handleUserLockedEvent_savesOutboxEventWithPendingStatus() {
    when(tokenService.generateToken()).thenReturn("reset-token");
    when(userEventFactory.createUserLockedEvent(mockUser, "reset-token")).thenReturn(userLockedEvent("reset-token"));

    userEventService.handleUserLockedEvent(mockUser);

    assertEquals(OutboxEventStatus.PENDING, captureOutboxEvent().getStatus());
  }

  @Test
  void handleUserLockedEvent_outboxEventIdMatchesDomainEventId() {
    when(tokenService.generateToken()).thenReturn("reset-token");
    UserLockedDomainEvent event = userLockedEvent("reset-token");
    when(userEventFactory.createUserLockedEvent(mockUser, "reset-token")).thenReturn(event);

    userEventService.handleUserLockedEvent(mockUser);

    assertEquals(event.getEventId(), captureOutboxEvent().getEventId());
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private OutboxEvent captureOutboxEvent() {
    ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
    verify(outboxEventRepository).save(captor.capture());
    return captor.getValue();
  }

  private UserCreatedDomainEvent userCreatedEvent() {
    return new UserCreatedDomainEvent(EXCHANGE, QUEUE, ROUTING,
      mockUser.getId().getValue(), mockUser.getName().getValue(),
      mockUser.getLastName().getValue(), mockUser.getEmail().getEmail());
  }

  private UserDeletedDomainEvent userDeletedEvent() {
    return new UserDeletedDomainEvent(EXCHANGE, QUEUE, ROUTING,
      mockUser.getId().getValue(), mockUser.getName().getValue(),
      mockUser.getLastName().getValue(), mockUser.getEmail().getEmail());
  }

  private UserLoggedDomainEvent userLoggedEvent() {
    return new UserLoggedDomainEvent(EXCHANGE, QUEUE, ROUTING,
      mockUser.getId().getValue(), mockUser.getName().getValue(),
      mockUser.getLastName().getValue(), mockUser.getEmail().getEmail());
  }

  private UserLogoutDomainEvent userLogoutEvent() {
    return new UserLogoutDomainEvent(EXCHANGE, QUEUE, ROUTING,
      mockUser.getId().getValue(), mockUser.getName().getValue(),
      mockUser.getLastName().getValue(), mockUser.getEmail().getEmail());
  }

  private UserLockedDomainEvent userLockedEvent(String token) {
    return new UserLockedDomainEvent(EXCHANGE, QUEUE, ROUTING,
      mockUser.getId().getValue(), mockUser.getName().getValue(),
      mockUser.getLastName().getValue(), mockUser.getEmail().getEmail(),
      token, LocalDateTime.now().plusMinutes(15));
  }
}
