package org.example.eventhub.dto.event

import org.example.eventhub.enums.EventStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class EventFilter(
    val title: String?,
    val city: String?,
    val minCapacity: Int?,
    val maxCapacity: Int?,
    val minPrice: BigDecimal?,
    val maxPrice: BigDecimal?,
    val eventStatus: EventStatus?,
    val fromDateTime: LocalDateTime?,
    val toDateTime: LocalDateTime?,
    val upcoming: Boolean?
)
