package org.example.eventhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.example.eventhub.enums.Role;
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

    @BeforeEach
    void setUp() {
        user = new User(10L, "john_doe", "mail", "pass", Role.USER, null, new ArrayList<>());

        ticket1 = new Ticket();
        ticket2 = new Ticket();

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalPrice(BigDecimal.valueOf(3500));
        order.setStatus(OrderStatus.PENDING);
        order.setTickets(List.of(ticket1, ticket2));
    }

    @Test
    void toLongDto_shouldReturnCorrectDto() {
        UserResponseShort userShort = new UserResponseShort("john_doe");

        when(userMapper.toShortDto(user)).thenReturn(userShort);
        when(ticketMapper.toShortDto(ticket1)).thenReturn(mock(TicketResponseShort.class));
        when(ticketMapper.toShortDto(ticket2)).thenReturn(mock(TicketResponseShort.class));

        OrderResponseLong dto = orderMapper.toLongDto(order);

        assertEquals(1L, dto.id());
        assertEquals(userShort, dto.user());
        assertEquals(2, dto.tickets().size());
        assertEquals(BigDecimal.valueOf(3500), dto.totalPrice());
        assertEquals(OrderStatus.PENDING, dto.orderStatus());
    }
}
