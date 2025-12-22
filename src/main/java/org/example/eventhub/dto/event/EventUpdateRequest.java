package org.example.eventhub.dto.event;

import jakarta.validation.constraints.Positive;
import org.example.eventhub.entity.Location;
import org.example.eventhub.enums.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventUpdateRequest(
        String title, //TODO сделать проверку что не пустая
        String description,
        LocalDateTime dateTime,
        Location location, //TODO поменять на DTO
        @Positive(message = "capacity должна быть положительна")
        Integer capacity,
        BigDecimal price,
        EventStatus eventStatus
) {
}
