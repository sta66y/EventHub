package org.example.eventhub.dto.user

import org.example.eventhub.enums.Role
import java.time.LocalDateTime

data class UserResponseLong(
    val id: Long?,
    val username: String,
    val email: String,
    val role: Role,
    val createdAt: LocalDateTime?,
    val countOrganizedEvents: Long
)
