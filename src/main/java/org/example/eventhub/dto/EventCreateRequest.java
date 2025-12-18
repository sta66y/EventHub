package org.example.eventhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.eventhub.entity.Location;
import org.example.eventhub.enums.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventCreateRequest(
        @NotBlank(message = "Title обязателен")
        String title,
        String description,
        @NotNull(message = "dateTime обязателен")
        LocalDateTime dateTime,
        Location location,
        @NotNull (message = "capacity обязательна")
        @Positive (message = "capacity должна быть положительна")
        Integer capacity,
        BigDecimal price,
        EventStatus eventStatus
){}
