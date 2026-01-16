package org.example.eventhub.mapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.dto.user.UserResponseLong
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.entity.User
import org.example.eventhub.enums.Role
import java.time.LocalDateTime

class UserMapperTest : StringSpec({

    val mapper = UserMapper()

    lateinit var user: User

    beforeEach {
        user = User(
            id = 1L,
            username = "username",
            email = "mail@mail.com",
            password = "hashed",
            role = Role.USER,
            createdAt = LocalDateTime.of(2023, 1, 1, 0, 0),
            organizedEvents = mutableListOf()
        )
    }

    "toShortDto маппит user в short dto" {
        val dto: UserResponseShort = mapper.toShortDto(user)

        dto.username shouldBe "username"
    }

    "toLongDto маппит user в long dto" {
        val dto: UserResponseLong = mapper.toLongDto(user)

        dto.id shouldBe 1L
        dto.username shouldBe "username"
        dto.email shouldBe "mail@mail.com"
        dto.role shouldBe Role.USER
        dto.createdAt shouldBe user.createdAt
        dto.countOrganizedEvents shouldBe 0L
    }

    "toEntity маппит RegisterRequest в User" {
        val dto = RegisterRequest(
            username = "newUser",
            email = "new@mail.com",
            password = "raw"
        )

        val entity = mapper.toEntity(dto, "hashedPassword")

        entity.username shouldBe "newUser"
        entity.email shouldBe "new@mail.com"
        entity.password shouldBe "hashedPassword"
        entity.role shouldBe Role.USER
    }
})
