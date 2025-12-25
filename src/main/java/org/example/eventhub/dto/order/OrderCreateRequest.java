package org.example.eventhub.dto.order;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderCreateRequest(
        @NotNull(message = "Хотя бы одно мероприятие должно быть в заказе") List<Long> eventsId) {}
