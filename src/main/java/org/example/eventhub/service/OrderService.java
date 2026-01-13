package org.example.eventhub.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.order.OrderCreateRequest;
import org.example.eventhub.dto.order.OrderResponseLong;
import org.example.eventhub.dto.order.OrderResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.OrderStatus;
import org.example.eventhub.enums.TicketStatus;
import org.example.eventhub.exception.OrderNotFoundException;
import org.example.eventhub.mapper.OrderMapper;
import org.example.eventhub.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final UserService userService;
    private final TicketService ticketService;

    private final OrderMapper mapper;

    private final OrderRepository repository;

    @Transactional
    public OrderResponseLong createOrder(OrderCreateRequest dto, Long userId) {
        User user = userService.getUserByIdAsEntity(userId);

        Order order = Order.builder()
                .user(user)
                .reservedUntil(LocalDateTime.now().plusMinutes(15))
                .build();

        reserveTickets(order, dto.eventsId(), userId);

        return mapper.toLongDto(repository.save(order));
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cancelExpiredReservations() {
        List<Order> expiredOrders =
                repository.findOrdersByStatusAndReservedUntilBefore(OrderStatus.PENDING, LocalDateTime.now());
        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            order.getTickets().forEach(t -> {
                if (t.getStatus() == TicketStatus.RESERVED) {
                    t.getEvent().decrementReservedCount();
                    t.setStatus(TicketStatus.CANCELLED);
                }
            });
        }
    }

    @Transactional
    public void payOrder(Long orderId) {
        Order order = getOrderByIdAsEntity(orderId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new IllegalStateException("Нельзя оплатить заказ в статусе " + order.getStatus());

        order.setStatus(OrderStatus.PAID);
        order.getTickets().forEach(t -> t.setStatus(TicketStatus.PAID));
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderByIdAsEntity(orderId);

        order.setStatus(OrderStatus.CANCELLED);
        order.getTickets().forEach(t -> {
            if (t.getStatus() != TicketStatus.CANCELLED) {
                t.setStatus(TicketStatus.CANCELLED);
                t.getEvent().decrementReservedCount();
            }
        });
    }

    private void reserveTickets(Order order, List<Long> eventIds, Long userId) {
        List<Ticket> tickets = new ArrayList<>();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (int i = 0; i < eventIds.size(); i++) {
            Ticket ticket = ticketService.createTicket(eventIds.get(i), userId, order);
            tickets.add(ticket);
            totalPrice = totalPrice.add(ticket.getPrice());
        }

        order.setTotalPrice(totalPrice);
        order.setTickets(tickets);
    }

    public OrderResponseLong getOrderById(Long id) {
        Order order = getOrderByIdAsEntity(id);

        return mapper.toLongDto(order);
    }

    public Page<OrderResponseShort> getAllOrders(Long userId, Pageable pageable) {
        return repository.findAllByUserId(userId, pageable).map(mapper::toShortDto);
    }

    private Order getOrderByIdAsEntity(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Ордера с id " + id + " не существует"));
    }
}
