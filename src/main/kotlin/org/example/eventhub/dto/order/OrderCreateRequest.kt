package org.example.eventhub.dto.order

import jakarta.validation.constraints.NotNull

data class OrderCreateRequest(
    val eventsId: @NotNull(message = "Хотя бы одно мероприятие должно быть в заказе") List<Long>
)
