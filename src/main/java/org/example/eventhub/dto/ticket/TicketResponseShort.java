package org.example.eventhub.dto.ticket;

import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.enums.TicketStatus;

public record TicketResponseShort(EventResponseShort event, UserResponseShort user, TicketStatus ticketStatus) {}
