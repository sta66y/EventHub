package org.example.eventhub.dto.user;

import java.time.LocalDateTime;
import org.example.eventhub.enums.Role;

public record UserResponseLong(
        Long id, String username, String email, Role role, LocalDateTime createdAt, long countOrganizedEvents) {}
