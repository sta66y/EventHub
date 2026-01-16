package org.example.eventhub.dto.ticket

import org.example.eventhub.dto.event.EventResponseShort
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.enums.TicketStatus
import java.math.BigDecimal

data class TicketResponseLong(
    val id: Long?,
    val event: EventResponseShort,
    val user: UserResponseShort,
    val orderId: Long?,
    val price: BigDecimal,
    val ticketStatus: TicketStatus
)
