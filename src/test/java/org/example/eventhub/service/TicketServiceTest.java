package org.example.eventhub.service;

import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.exception.NoAvailableTicketsException;
import org.example.eventhub.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    private static final Long EVENT_ID = 100L;
    private static final Long USER_ID = 10L;
    private static final Long ORDER_ID = 1L;

    private final TicketRepository repository = mock(TicketRepository.class);
    private final UserService userService = mock(UserService.class);
    private final EventService eventService = mock(EventService.class);

    private final TicketService ticketService = new TicketService(repository, userService, eventService);

    private User user;
    private Event event;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);

        event = new Event();
        event.setId(EVENT_ID);
        event.setCapacity(100);
        event.setPrice(BigDecimal.valueOf(2500));

        order = Order.builder()
                .id(ORDER_ID)
                .build();
    }

    @Test
    @DisplayName("createTicket: успешно создаёт тикет, если есть свободные места")
    void createTicket_success_whenTicketsAvailable() {
        when(userService.getUserByIdAsEntity(USER_ID)).thenReturn(user);
        when(eventService.getEventByIdAsEntity(EVENT_ID)).thenReturn(event);

        when(repository.countActiveByEvent(event)).thenReturn(50);

        Ticket result = ticketService.createTicket(EVENT_ID, USER_ID, order);

        assertNotNull(result);
        assertEquals(order, result.getOrder());
        assertEquals(event, result.getEvent());
        assertEquals(user, result.getUser());
        assertEquals(event.getPrice(), result.getPrice());

        verify(userService).getUserByIdAsEntity(USER_ID);
        verify(eventService).getEventByIdAsEntity(EVENT_ID);
        verify(repository).countActiveByEvent(event);
    }

    @Test
    @DisplayName("createTicket: бросает NoAvailableTicketsException, если билетов нет")
    void createTicket_throwsException_whenNoTicketsAvailable() {
        when(userService.getUserByIdAsEntity(USER_ID)).thenReturn(user);
        when(eventService.getEventByIdAsEntity(EVENT_ID)).thenReturn(event);
        when(repository.countActiveByEvent(event)).thenReturn(100);

        NoAvailableTicketsException ex = assertThrows(
                NoAvailableTicketsException.class,
                () -> ticketService.createTicket(EVENT_ID, USER_ID, order)
        );

        assertEquals(
                "Все билеты на мероприятие с id " + EVENT_ID + " уже распроданы",
                ex.getMessage()
        );

        verify(userService).getUserByIdAsEntity(USER_ID);
        verify(eventService).getEventByIdAsEntity(EVENT_ID);
        verify(repository).countActiveByEvent(event);
    }

    @Test
    @DisplayName("createTicket: бросает NoAvailableTicketsException, если продано больше capacity")
    void createTicket_throwsException_whenOverCapacity() {
        when(userService.getUserByIdAsEntity(USER_ID)).thenReturn(user);
        when(eventService.getEventByIdAsEntity(EVENT_ID)).thenReturn(event);

        when(repository.countActiveByEvent(event)).thenReturn(101);

        assertThrows(NoAvailableTicketsException.class,
                () -> ticketService.createTicket(EVENT_ID, USER_ID, order));

        verify(repository).countActiveByEvent(event);
    }
}