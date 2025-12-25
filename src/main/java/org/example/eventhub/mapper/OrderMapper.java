package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.order.OrderResponseLong;
import org.example.eventhub.dto.order.OrderResponseShort;
import org.example.eventhub.entity.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;

    public OrderResponseLong toLongDto(Order entity) {
        return new OrderResponseLong(
                entity.getId(),
                userMapper.toShortDto(entity.getUser()),
                entity.getTickets().stream().map(ticketMapper::toShortDto).toList(),
                entity.getTotalPrice(),
                entity.getStatus());
    }

    public OrderResponseShort toShortDto(Order entity) {
        return new OrderResponseShort(
                entity.getTickets().stream().map(ticketMapper::toShortDto).toList(),
                entity.getTotalPrice(),
                entity.getStatus());
    }
}
