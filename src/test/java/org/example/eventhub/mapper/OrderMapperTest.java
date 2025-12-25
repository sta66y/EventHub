package org.example.eventhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.dto.order.OrderResponseLong;
import org.example.eventhub.dto.order.OrderResponseShort;
import org.example.eventhub.dto.ticket.TicketResponseShort;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.OrderStatus;
import org.example.eventhub.enums.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private OrderMapper orderMapper;

    private User user;
    private Ticket ticket1;
    private Ticket ticket2;
    private Order order;

    private UserResponseShort userShort;
    private EventResponseShort eventShort;
    private TicketResponseShort ticketShort1;
    private TicketResponseShort ticketShort2;

    @BeforeEach
    void setUp() {
        user = User.builder().id(10L).username("john_doe").build();

        ticket1 = new Ticket();
        ticket2 = new Ticket();

        order = Order.builder()
                .id(1L)
                .user(user)
                .totalPrice(BigDecimal.valueOf(3500))
                .status(OrderStatus.PENDING)
                .tickets(List.of(ticket1, ticket2))
                .build();

        userShort = new UserResponseShort("john_doe");
        eventShort = new EventResponseShort(100L, "Концерт", LocalDateTime.now());

        ticketShort1 = new TicketResponseShort(eventShort, userShort, TicketStatus.RESERVED);
        ticketShort2 = new TicketResponseShort(eventShort, userShort, TicketStatus.RESERVED);
    }

    @Test
    @DisplayName("toLongDto: возвращает правильный Long DTO с пользователем и тикетами")
    void toLongDto_shouldReturnCorrectDto() {
        when(userMapper.toShortDto(user)).thenReturn(userShort);
        when(ticketMapper.toShortDto(ticket1)).thenReturn(ticketShort1);
        when(ticketMapper.toShortDto(ticket2)).thenReturn(ticketShort2);

        OrderResponseLong expected = new OrderResponseLong(
                1L, userShort, List.of(ticketShort1, ticketShort2), BigDecimal.valueOf(3500), OrderStatus.PENDING);

        OrderResponseLong actual = orderMapper.toLongDto(order);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("toShortDto: возвращает правильный Short DTO с тикетами, но без пользователя и id")
    void toShortDto_shouldReturnCorrectDto() {
        when(ticketMapper.toShortDto(ticket1)).thenReturn(ticketShort1);
        when(ticketMapper.toShortDto(ticket2)).thenReturn(ticketShort2);

        OrderResponseShort expected = new OrderResponseShort(
                List.of(ticketShort1, ticketShort2), BigDecimal.valueOf(3500), OrderStatus.PENDING);

        OrderResponseShort actual = orderMapper.toShortDto(order);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("toLongDto: работает с пустым списком тикетов")
    void toLongDto_handlesEmptyTicketsList() {
        when(userMapper.toShortDto(user)).thenReturn(userShort);
        order.setTickets(List.of());

        OrderResponseLong expected =
                new OrderResponseLong(1L, userShort, List.of(), BigDecimal.valueOf(3500), OrderStatus.PENDING);

        assertEquals(expected, orderMapper.toLongDto(order));
    }

    @Test
    @DisplayName("toShortDto: работает с пустым списком тикетов")
    void toShortDto_handlesEmptyTicketsList() {
        order.setTickets(List.of());

        OrderResponseShort expected = new OrderResponseShort(List.of(), BigDecimal.valueOf(3500), OrderStatus.PENDING);

        assertEquals(expected, orderMapper.toShortDto(order));
    }
}
