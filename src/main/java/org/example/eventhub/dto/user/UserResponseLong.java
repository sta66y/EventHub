package org.example.eventhub.dto.user;

import org.example.eventhub.enums.Role;

import java.time.LocalDateTime;

public record UserResponseLong(
        Long id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt,
        long countOrganizedEvents
) {
}
