package org.example.eventhub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.dto.order.OrderCreateRequest;
import org.example.eventhub.dto.order.OrderResponseLong;
import org.example.eventhub.dto.order.OrderResponseShort;
import org.example.eventhub.dto.ticket.TicketResponseShort;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.OrderStatus;
import org.example.eventhub.enums.TicketStatus;
import org.example.eventhub.exception.OrderNotFoundException;
import org.example.eventhub.mapper.OrderMapper;
import org.example.eventhub.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

class OrderServiceTest {

    private static final Long EXISTING_ORDER_ID = 1L;
    private static final Long NON_EXISTING_ORDER_ID = 999L;
    private static final Long USER_ID = 10L;

    private final UserService userService = mock(UserService.class);
    private final TicketService ticketService = mock(TicketService.class);
    private final OrderMapper mapper = mock(OrderMapper.class);
    private final OrderRepository repository = mock(OrderRepository.class);

    private final OrderService orderService = new OrderService(userService, ticketService, mapper, repository);

    private User user;
    private Ticket ticket1;
    private Ticket ticket2;
    private Order order;
    private OrderCreateRequest createRequest;

    private UserResponseShort userShort;
    private EventResponseShort eventShort1;
    private EventResponseShort eventShort2;
    private TicketResponseShort ticketShort1;
    private TicketResponseShort ticketShort2;

    private OrderResponseLong responseLong;
    private OrderResponseShort responseShort;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);

        Event event = new Event();

        ticket1 = Ticket.builder()
                .price(BigDecimal.valueOf(1000))
                .event(event)
                .build();

        ticket2 = Ticket.builder()
                .price(BigDecimal.valueOf(2500))
                .event(event)
                .build();

        order = Order.builder()
                .id(EXISTING_ORDER_ID)
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(3500))
                .tickets(List.of(ticket1, ticket2))
                .build();

        createRequest = new OrderCreateRequest(List.of(100L, 200L));

        eventShort1 = new EventResponseShort(100L, "Rock Concert", LocalDateTime.of(2026, 1, 15, 20, 0));

        eventShort2 = new EventResponseShort(200L, "Jazz Evening", LocalDateTime.of(2026, 2, 20, 19, 30));

        userShort = new UserResponseShort("john_doe");

        ticketShort1 = new TicketResponseShort(eventShort1, userShort, TicketStatus.RESERVED);
        ticketShort2 = new TicketResponseShort(eventShort2, userShort, TicketStatus.RESERVED);

        responseLong = new OrderResponseLong(
                EXISTING_ORDER_ID,
                userShort,
                List.of(ticketShort1, ticketShort2),
                BigDecimal.valueOf(3500),
                OrderStatus.PENDING);

        responseShort = new OrderResponseShort(
                List.of(ticketShort1, ticketShort2), BigDecimal.valueOf(3500), OrderStatus.PENDING);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("createOrder: успешно создаёт заказ с двумя тикетами, считает сумму и возвращает Long DTO")
    void createOrder_success() {
        when(userService.getUserByIdAsEntity(USER_ID)).thenReturn(user);

        when(ticketService.createTicket(eq(100L), eq(USER_ID), any(Order.class)))
                .thenReturn(ticket1);
        when(ticketService.createTicket(eq(200L), eq(USER_ID), any(Order.class)))
                .thenReturn(ticket2);

        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(EXISTING_ORDER_ID);
            return saved;
        });

        when(mapper.toLongDto(any(Order.class))).thenReturn(responseLong);

        OrderResponseLong result = orderService.createOrder(createRequest, USER_ID);

        assertEquals(responseLong, result);
        assertEquals(2, result.tickets.size());
        assertEquals(BigDecimal.valueOf(3500), result.totalPrice);
        assertEquals(userShort, result.user);

        verify(ticketService, times(2)).createTicket(anyLong(), eq(USER_ID), any(Order.class));
        verify(repository)
                .save(argThat(
                        o -> o.getTickets().size() == 2 && o.getTotalPrice().compareTo(BigDecimal.valueOf(3500)) == 0));
    }

    @Test
    @DisplayName("getOrderById: возвращает полный Long DTO с пользователем и тикетами")
    void getOrderById_success() {
        when(repository.findById(EXISTING_ORDER_ID)).thenReturn(Optional.of(order));
        when(mapper.toLongDto(order)).thenReturn(responseLong);

        OrderResponseLong result = orderService.getOrderById(EXISTING_ORDER_ID);

        assertEquals(responseLong, result);
        assertEquals(userShort, result.user);
        assertEquals(2, result.tickets.size());
    }

    @Test
    @DisplayName("getOrderById: бросает исключение, если заказ не найден")
    void getOrderById_notFound() {
        when(repository.findById(NON_EXISTING_ORDER_ID)).thenReturn(Optional.empty());

        OrderNotFoundException ex =
                assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(NON_EXISTING_ORDER_ID));

        assertEquals("Ордера с id " + NON_EXISTING_ORDER_ID + " не существует", ex.getMessage());
    }

    @Test
    @DisplayName("getAllOrders: возвращает страницу Short DTO с тикетами и статусом")
    void getAllOrders_returnsPage() {
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(repository.findAllByUserId(USER_ID, pageable)).thenReturn(page);
        when(mapper.toShortDto(order)).thenReturn(responseShort);

        Page<OrderResponseShort> result = orderService.getAllOrders(USER_ID, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseShort, result.getContent().get(0));
        assertEquals(2, result.getContent().get(0).tickets.size());
        assertEquals(BigDecimal.valueOf(3500), result.getContent().get(0).totalPrice);
    }

    @Test
    @DisplayName("getAllOrders: возвращает пустую страницу, если заказов нет")
    void getAllOrders_emptyPage() {
        Page<Order> emptyPage = Page.empty(pageable);

        when(repository.findAllByUserId(USER_ID, pageable)).thenReturn(emptyPage);

        Page<OrderResponseShort> result = orderService.getAllOrders(USER_ID, pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("cancelOrder: бросает исключение, если нечего отменять")
    void cancelOrder_fail() {
        when(repository.findById(EXISTING_ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(EXISTING_ORDER_ID));
    }

    @Test
    @DisplayName("cancelOrder: бросает исключение, если статус event не Pending")
    void cancelOrder_otherStatus() {
        Order cancelledOrder = Order.builder()
                .status(OrderStatus.CANCELLED)
                .build();
        when(repository.findById(EXISTING_ORDER_ID)).thenReturn(Optional.of(cancelledOrder));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(EXISTING_ORDER_ID));
    }

    @Test
    @DisplayName("cancelOrder: успешно отменяет заказ и билеты")
    void cancelOrder_success() {
        Event event = new Event();
        event.setReservedCount(2);

        Ticket ticket = Ticket.builder()
                .status(TicketStatus.RESERVED)
                .event(event)
                .build();

        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .tickets(List.of(ticket))
                .build();

        when(repository.findById(EXISTING_ORDER_ID)).thenReturn(Optional.of(order));

        orderService.cancelOrder(EXISTING_ORDER_ID);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(TicketStatus.CANCELLED, ticket.getStatus());
        assertEquals(1, event.getReservedCount());
    }


    @Test
    @DisplayName("cancelOrder: бросает исключение, если заказ не найден")
    void cancelOrder_notFound() {
        when(repository.findById(NON_EXISTING_ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(NON_EXISTING_ORDER_ID));
    }

    @Test
    @DisplayName("cancelExpiredReservations: отменяет просроченные заказы и билеты")
    void cancelExpiredReservations_success() {
        Event event = new Event();
        event.setReservedCount(2);

        Ticket reservedTicket = Ticket.builder()
                .status(TicketStatus.RESERVED)
                .event(event)
                .build();

        Order expiredOrder = Order.builder()
                .status(OrderStatus.PENDING)
                .reservedUntil(LocalDateTime.now().minusMinutes(1))
                .tickets(List.of(reservedTicket))
                .build();

        when(repository.findOrdersByStatusAndReservedUntilBefore(
                eq(OrderStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredOrder));

        orderService.cancelExpiredReservations();

        assertEquals(OrderStatus.CANCELLED, expiredOrder.getStatus());
        assertEquals(TicketStatus.CANCELLED, reservedTicket.getStatus());
        assertEquals(1, event.getReservedCount());
    }

    @Test
    @DisplayName("payOrder: успешно переводит заказ и билеты в PAID")
    void payOrder_success() {
        Ticket ticket = Ticket.builder()
                .status(TicketStatus.RESERVED)
                .build();

        Order order = Order.builder()
                .id(EXISTING_ORDER_ID)
                .status(OrderStatus.PENDING)
                .tickets(List.of(ticket))
                .build();

        when(repository.findById(EXISTING_ORDER_ID)).thenReturn(Optional.of(order));

        orderService.payOrder(EXISTING_ORDER_ID);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(TicketStatus.PAID, ticket.getStatus());
    }

    @Test
    @DisplayName("payOrder: бросает исключение, если статус не PENDING")
    void payOrder_wrongStatus() {
        Order order = Order.builder()
                .status(OrderStatus.CANCELLED)
                .build();

        when(repository.findById(EXISTING_ORDER_ID)).thenReturn(Optional.of(order));

        IllegalStateException ex =
                assertThrows(IllegalStateException.class,
                        () -> orderService.payOrder(EXISTING_ORDER_ID));

        assertTrue(ex.getMessage().contains("Нельзя оплатить заказ"));
    }


}
