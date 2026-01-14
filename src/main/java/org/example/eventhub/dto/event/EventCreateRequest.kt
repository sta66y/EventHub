package org.example.eventhub.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.eventhub.dto.location.LocationCreateRequest;
import org.example.eventhub.enums.EventStatus;

public record EventCreateRequest(
        @NotBlank(message = "Title обязателен") String title,
        String description,
        @NotNull(message = "dateTime обязателен") LocalDateTime dateTime,
        LocationCreateRequest location,
        @NotNull(message = "capacity обязательна") @Positive(message = "capacity должна быть положительна")
                Integer capacity,
        BigDecimal price,
        EventStatus eventStatus) {}
