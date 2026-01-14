package org.example.eventhub.dto.order

import org.example.eventhub.dto.ticket.TicketResponseShort
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.enums.OrderStatus
import java.math.BigDecimal

data class OrderResponseLong(
    val id: Long?,
    val user: UserResponseShort,
    val tickets: List<TicketResponseShort>,
    val totalPrice: BigDecimal,
    val orderStatus: OrderStatus
)
