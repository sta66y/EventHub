package org.example.eventhub.dto.ticket

import jakarta.validation.constraints.NotNull

data class TicketCreateRequest(
    val eventId: @NotNull(message = "eventId обязателен") Long
)
