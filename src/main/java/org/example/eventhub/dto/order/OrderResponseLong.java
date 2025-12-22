package org.example.eventhub.dto.order;

import org.example.eventhub.dto.ticket.TicketResponseShort;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseLong(
        Long id,
        UserResponseShort user,
        List<TicketResponseShort> tickets,
        BigDecimal totalPrice,
        OrderStatus orderStatus
) {
}
