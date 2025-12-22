package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.ticket.TicketCreateRequest;
import org.example.eventhub.dto.ticket.TicketResponseLong;
import org.example.eventhub.dto.ticket.TicketResponseShort;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.service.EventService;
import org.springframework.stereotype.Component;

import org.example.eventhub.entity.User;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    private final EventService eventService;

    public TicketResponseLong toLongDto(Ticket entity) {
        return new TicketResponseLong(
                entity.getId(),
                eventMapper.toShortDto(entity.getEvent()),
                userMapper.toShortDto(entity.getUser()),
                entity.getOrder().getId(),
                entity.getPrice(),
                entity.getStatus()
        );
    }

    public TicketResponseShort toShortDto(Ticket entity) {
        return new TicketResponseShort(
                eventMapper.toShortDto(entity.getEvent()),
                userMapper.toShortDto(entity.getUser()),
                entity.getStatus()
        );
    }
}
