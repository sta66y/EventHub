package org.example.eventhub.dto.user;

import org.example.eventhub.enums.Role;

import java.time.LocalDateTime;

public record UserFilter(
    String username,
    Role role,
    LocalDateTime fromCreatedAt,
    LocalDateTime toCreatedAt,

    Integer minOrganizedEvents,
    Integer maxOrganizedEvents //TODO сортировку мб?
) {
}
