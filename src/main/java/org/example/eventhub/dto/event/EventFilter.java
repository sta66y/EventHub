package org.example.eventhub.dto.event;

import org.example.eventhub.enums.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventFilter(
        String title,
        String city,

        Integer minCapacity,
        Integer maxCapacity,

        BigDecimal minPrice,
        BigDecimal maxPrice,

        EventStatus eventStatus,

        LocalDateTime fromDateTime,
        LocalDateTime toDateTime,
        Boolean upcoming
) {
}
