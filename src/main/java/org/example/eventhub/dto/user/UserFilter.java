package org.example.eventhub.dto.user;

import java.time.LocalDateTime;
import org.example.eventhub.enums.Role;

public record UserFilter(
        String username,
        Role role,
        LocalDateTime fromCreatedAt,
        LocalDateTime toCreatedAt,
        Integer minOrganizedEvents,
        Integer maxOrganizedEvents // TODO сортировку мб?
        ) {}
