package org.example.eventhub.mapper

import org.example.eventhub.dto.order.OrderResponseLong
import org.example.eventhub.dto.order.OrderResponseShort
import org.example.eventhub.entity.Order
import org.springframework.stereotype.Component

@Component
class OrderMapper(
    private val userMapper: UserMapper,
    private val ticketMapper: TicketMapper
) {

    fun toLongDto(entity: Order): OrderResponseLong =
        _root_ide_package_.org.example.eventhub.dto.order.OrderResponseLong(
            id = entity.id,
            user = userMapper.toShortDto(entity.user),
            tickets = entity.tickets.map { ticketMapper.toShortDto(it) },
            totalPrice = entity.totalPrice,
            orderStatus = entity.status
        )

    fun toShortDto(entity: Order): OrderResponseShort =
        _root_ide_package_.org.example.eventhub.dto.order.OrderResponseShort(
            tickets = entity.tickets.map { ticketMapper.toShortDto(it) },
            totalPrice = entity.totalPrice,
            orderStatus = entity.status
        )
}
