package org.example.eventhub.service;
import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.order.OrderCreateRequest;
import org.example.eventhub.dto.order.OrderResponseLong;
import org.example.eventhub.dto.order.OrderResponseShort;
import org.example.eventhub.dto.ticket.TicketCreateRequest;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.OrderStatus;
import org.example.eventhub.exception.OrderNotFoundException;
import org.example.eventhub.mapper.OrderMapper;
import org.example.eventhub.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final UserService userService;
    private final TicketService ticketService;

    private final OrderMapper mapper;

    private final OrderRepository repository;


    public OrderResponseLong createOrder(OrderCreateRequest dto, Long userId) {
        User user = userService.getUserByIdAsEntity(userId);

        Order order = Order.builder()
                .user(user)
                .build();

        List<Ticket> tickets = new ArrayList<>();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (int i = 0; i < dto.eventsId().size(); i++) {
            Ticket ticket = ticketService.createTicket(dto.eventsId().get(i), userId, order);
            tickets.add(ticket);
            totalPrice = totalPrice.add(ticket.getPrice());
        }

        order.setTotalPrice(totalPrice);
        order.setTickets(tickets);

        return mapper.toLongDto(repository.save(order));
    }

    public OrderResponseLong getOrderById(Long id) {
        Order order = getOrderByIdAsEntity(id);

        return mapper.toLongDto(order);
    }

    public Page<OrderResponseShort> getAllOrders(Long userId, Pageable pageable) { //TODO фильтрация
        return repository.findAllByUserId(userId, pageable).map(mapper::toShortDto);
    }

    public OrderResponseLong cancelOrder(Long id) {
        Order order = getOrderByIdAsEntity(id);

        order.setStatus(OrderStatus.CANCELLED);

        return mapper.toLongDto(repository.save(order));
    }

    private Order getOrderByIdAsEntity(Long id) {
        return  repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Ордера с id " + id + " не существует"));
    }
}
