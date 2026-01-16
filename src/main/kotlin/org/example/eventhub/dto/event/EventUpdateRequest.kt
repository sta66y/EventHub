package org.example.eventhub.dto.event

import jakarta.validation.constraints.Positive
import org.example.eventhub.dto.location.LocationUpdateRequest
import org.example.eventhub.enums.EventStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class EventUpdateRequest(
    val title: String?,  // TODO сделать проверку что не пустая
    val description: String?,
    val dateTime: LocalDateTime?,
    val location: LocationUpdateRequest?,
    val capacity: @Positive(message = "capacity должна быть положительна") Int?,
    val price: BigDecimal?,
    val eventStatus: EventStatus?
)
