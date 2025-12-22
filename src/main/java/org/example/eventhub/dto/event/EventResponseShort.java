package org.example.eventhub.dto.event;

import java.time.LocalDateTime;

public record EventResponseShort(
        Long id,
        String title,
        LocalDateTime dateTime
){
}
