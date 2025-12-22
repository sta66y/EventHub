package org.example.eventhub.dto.ticket;

import jakarta.validation.constraints.NotNull;

public record TicketCreateRequest(
     @NotNull(message = "eventId обязателен")
     Long eventId
) {
}
