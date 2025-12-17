package org.example.eventhub.dto;

import org.example.eventhub.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponseLong(
        Long id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt,
        List<EventResponseShort> organizedEvents
) {
}
