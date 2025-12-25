package org.example.eventhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.dto.ticket.TicketResponseLong;
import org.example.eventhub.dto.ticket.TicketResponseShort;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketMapperTest {

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private TicketMapper ticketMapper;

    private User user;
    private Event event;
    private Order order;
    private Ticket ticket;

    private UserResponseShort userShort;
    private EventResponseShort eventShort;

    @BeforeEach
    void setUp() {
        user = User.builder().id(10L).username("john_doe").build();

        event = Event.builder().id(100L).title("Concert").build();

        order = Order.builder().id(1L).build();

        ticket = Ticket.builder()
                .id(1000L)
                .event(event)
                .user(user)
                .order(order)
                .price(BigDecimal.valueOf(2500))
                .status(TicketStatus.RESERVED)
                .build();

        userShort = new UserResponseShort("john_doe");
        eventShort = new EventResponseShort(100L, "Concert", null);
        when(userMapper.toShortDto(user)).thenReturn(userShort);
        when(eventMapper.toShortDto(event)).thenReturn(eventShort);
    }

    @Test
    @DisplayName("toLongDto: возвращает правильный Long DTO со всеми полями")
    void toLongDto_shouldReturnCorrectDto() {
        TicketResponseLong expected = new TicketResponseLong(
                1000L, eventShort, userShort, 1L, BigDecimal.valueOf(2500), TicketStatus.RESERVED);

        assertEquals(expected, ticketMapper.toLongDto(ticket));
    }

    @Test
    @DisplayName("toShortDto: возвращает правильный Short DTO без id, orderId и price")
    void toShortDto_shouldReturnCorrectDto() {
        TicketResponseShort expected = new TicketResponseShort(eventShort, userShort, TicketStatus.RESERVED);

        assertEquals(expected, ticketMapper.toShortDto(ticket));
    }

    @Test
    @DisplayName("toLongDto: корректно маппит вложенные объекты через зависимости")
    void toLongDto_mapsNestedObjectsCorrectly() {
        ticketMapper.toLongDto(ticket);

        verify(eventMapper).toShortDto(event);
        verify(userMapper).toShortDto(user);
    }

    @Test
    @DisplayName("toShortDto: корректно маппит вложенные объекты через зависимости")
    void toShortDto_mapsNestedObjectsCorrectly() {
        ticketMapper.toShortDto(ticket);

        verify(eventMapper).toShortDto(event);
        verify(userMapper).toShortDto(user);
    }
}
