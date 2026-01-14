package org.example.eventhub.dto.event

import java.time.LocalDateTime

data class EventResponseShort(val id: Long?, val title: String, val dateTime: LocalDateTime)
