package org.example.eventhub.dto.order

import org.example.eventhub.dto.ticket.TicketResponseShort
import org.example.eventhub.enums.OrderStatus
import java.math.BigDecimal

data class OrderResponseShort(
    val tickets: List<TicketResponseShort>,
    val totalPrice: BigDecimal,
    val orderStatus: OrderStatus
)
