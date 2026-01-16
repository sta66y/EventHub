package org.example.eventhub.dto.event

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.example.eventhub.dto.location.LocationCreateRequest
import org.example.eventhub.enums.EventStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class EventCreateRequest(
    val title: @NotBlank(message = "Title обязателен") String,
    val description: String?,
    val dateTime: @NotNull(message = "dateTime обязателен") LocalDateTime,
    val location: LocationCreateRequest?,
    val capacity: @NotNull(message = "capacity обязательна") @Positive(message = "capacity должна быть положительна") Int,
    val price: BigDecimal?,
    val eventStatus: EventStatus?
)
