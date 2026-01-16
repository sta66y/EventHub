package org.example.eventhub.dto.user

import org.example.eventhub.enums.Role
import java.time.LocalDateTime

data class UserFilter(
    val username: String?,
    val role: Role?,
    val fromCreatedAt: LocalDateTime?,
    val toCreatedAt: LocalDateTime?,
    val minOrganizedEvents: Int?,
    val maxOrganizedEvents: Int? // TODO сортировку мб?
)
