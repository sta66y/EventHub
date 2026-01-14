package org.example.eventhub.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.eventhub.enums.EventStatus;

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
        Boolean upcoming) {}
