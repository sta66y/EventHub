package org.example.eventhub.mapper

import org.example.eventhub.dto.ticket.TicketResponseLong
import org.example.eventhub.dto.ticket.TicketResponseShort
import org.example.eventhub.entity.Ticket
import org.springframework.stereotype.Component

@Component
class TicketMapper (
    private val eventMapper: EventMapper,
    private val userMapper: UserMapper
){
    fun toLongDto(entity: Ticket): TicketResponseLong {
        return TicketResponseLong(
            id = entity.id,
            event = eventMapper.toShortDto(entity.event),
            user = userMapper.toShortDto(entity.user),
            orderId = entity.order.id,
            price = entity.price,
            ticketStatus = entity.status
        )
    }

    fun toShortDto(entity: Ticket): TicketResponseShort {
        return TicketResponseShort(
            event = eventMapper.toShortDto(entity.event),
            user = userMapper.toShortDto(entity.user),
            ticketStatus = entity.status
        )
    }
}