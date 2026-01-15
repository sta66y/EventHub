package org.example.eventhub.mapper

import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.dto.user.UserResponseLong
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.entity.User
import org.springframework.stereotype.Component

@Component
class UserMapper{

    fun toShortDto(entity: User): UserResponseShort =
        UserResponseShort(entity.username)

    fun toLongDto(entity: User): UserResponseLong =
        UserResponseLong(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            role = entity.role,
            createdAt =  entity.createdAt,
            countOrganizedEvents =  entity.organizedEvents.size.toLong()
        )

    fun toEntity(dto: RegisterRequest, password: String): User =
        User(
            username = dto.username,
            email = dto.email,
            password = password
        )
}