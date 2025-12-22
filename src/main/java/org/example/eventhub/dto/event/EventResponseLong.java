package org.example.eventhub.dto.event;

import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.Location;
import org.example.eventhub.enums.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponseLong(
        Long id,
        String title,
        String description,
        LocalDateTime dateTime,
        Location location,
        Integer capacity,
        BigDecimal price,
        EventStatus eventStatus,
        UserResponseShort organizer
) {}
