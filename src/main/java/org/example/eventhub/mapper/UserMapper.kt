package org.example.eventhub.mapper

import org.example.eventhub.dto.user.UserCreateRequest
import org.example.eventhub.dto.user.UserResponseLong
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.entity.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val passwordEncoder: PasswordEncoder
) {
    fun toShortDto(entity: User): UserResponseShort {
        return UserResponseShort(entity.username)
    }

    fun toLongDto(entity: User): UserResponseLong {
        return UserResponseLong(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            role = entity.role,
            createdAt =  entity.createdAt,
            countOrganizedEvents =  entity.organizedEvents.size.toLong()
        )
    }

    fun toEntity(dto: UserCreateRequest): User {
        return User(
            username = dto.username,
            email = dto.email,
            password = requireNotNull(passwordEncoder.encode(dto.password)) {
                "PasswordEncoder returned null"
            }
        )
    }

}