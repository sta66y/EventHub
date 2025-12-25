package org.example.eventhub.dto.event;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.eventhub.dto.location.LocationUpdateRequest;
import org.example.eventhub.enums.EventStatus;

public record EventUpdateRequest(
        String title, // TODO сделать проверку что не пустая
        String description,
        LocalDateTime dateTime,
        LocationUpdateRequest location,
        @Positive(message = "capacity должна быть положительна") Integer capacity,
        BigDecimal price,
        EventStatus eventStatus) {}
