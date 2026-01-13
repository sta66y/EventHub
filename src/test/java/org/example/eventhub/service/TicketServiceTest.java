package org.example.eventhub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.exception.NoAvailableTicketsException;
import org.example.eventhub.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        event.setCapacity(1);
        event.setPrice(BigDecimal.valueOf(2500));

        order = Order.builder().id(ORDER_ID).build();
    }

    @Test
    @DisplayName("createTicket: успешно создаёт билет, если есть свободные места")
    void createTicket_success_whenTicketsAvailable() {
        Ticket input = Ticket.builder()
            .event(event)
            .user(user)
            .order(order)
            .price(event.getPrice())
            .build();

        when(userService.getUserByIdAsEntity(USER_ID)).thenReturn(user);
        when(eventService.getEventByIdAsEntity(EVENT_ID)).thenReturn(event);
        doNothing().when(eventService).saveEvent(event);
        when(repository.save(any())).thenReturn(input);


        Ticket result = ticketService.createTicket(EVENT_ID, USER_ID, order);

        assertNotNull(result);
        assertEquals(order, result.getOrder());
        assertEquals(event, result.getEvent());
        assertEquals(user, result.getUser());
        assertEquals(event.getPrice(), result.getPrice());

        verify(userService).getUserByIdAsEntity(USER_ID);
        verify(eventService).getEventByIdAsEntity(EVENT_ID);
    }

    @Test
    @DisplayName("createTicket: бросает NoAvailableTicketsException, если билетов нет")
    void createTicket_throwsException_whenNoTicketsAvailable() {
        Event eventWithoutTickets = Event.builder()
                .id(EVENT_ID)
                .capacity(0)
                .build();

        when(userService.getUserByIdAsEntity(USER_ID)).thenReturn(user);
        when(eventService.getEventByIdAsEntity(EVENT_ID)).thenReturn(eventWithoutTickets);

       assertThrows(NoAvailableTicketsException.class, () -> ticketService.createTicket(EVENT_ID, USER_ID, order));


        verify(userService).getUserByIdAsEntity(USER_ID);
        verify(eventService).getEventByIdAsEntity(EVENT_ID);
    }
}
