package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.ticket.TicketResponseLong;
import org.example.eventhub.dto.ticket.TicketResponseShort;
import org.example.eventhub.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    public TicketResponseLong toLongDto(Ticket entity) {
        return new TicketResponseLong(
                entity.getId(),
                eventMapper.toShortDto(entity.getEvent()),
                userMapper.toShortDto(entity.getUser()),
                entity.getOrder().getId(),
                entity.getPrice(),
                entity.getStatus());
    }

    public TicketResponseShort toShortDto(Ticket entity) {
        return new TicketResponseShort(
                eventMapper.toShortDto(entity.getEvent()), userMapper.toShortDto(entity.getUser()), entity.getStatus());
    }
}
