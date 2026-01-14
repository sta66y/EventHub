package org.example.eventhub.dto.event

import org.example.eventhub.dto.location.LocationResponseLong
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.enums.EventStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class EventResponseLong(
    val id: Long?,
    val title: String,
    val description: String?,
    val dateTime: LocalDateTime,
    val location: LocationResponseLong?,
    val capacity: Int,
    val price: BigDecimal,
    val eventStatus: EventStatus,
    val organizer: UserResponseShort
)
