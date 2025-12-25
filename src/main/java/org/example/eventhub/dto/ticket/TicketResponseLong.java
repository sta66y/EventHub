package org.example.eventhub.dto.ticket;

import java.math.BigDecimal;
import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.enums.TicketStatus;

public record TicketResponseLong(
        Long id,
        EventResponseShort event,
        UserResponseShort user,
        Long orderId,
        BigDecimal price,
        TicketStatus ticketStatus) {}
