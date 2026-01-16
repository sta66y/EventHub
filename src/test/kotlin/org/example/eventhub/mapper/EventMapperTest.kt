package org.example.eventhub.mapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.eventhub.dto.event.EventCreateRequest
import org.example.eventhub.dto.event.EventUpdateRequest
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.entity.Event
import org.example.eventhub.enums.EventStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import org.example.eventhub.entity.User
import org.example.eventhub.enums.Role

class EventMapperTest : StringSpec({

    val userMapper = mockk<UserMapper>()
    val locationMapper = mockk<LocationMapper>()

    val eventMapper = EventMapper(userMapper, locationMapper)

    val user = User(
        username = "user",
        email = "email@mail.com",
        password = "password",
        role = Role.USER
    )

    lateinit var event: Event

    beforeEach {
        event = Event(
            id = 1,
            title = "Event Title",
            dateTime = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
            capacity = 1,
            price = BigDecimal(0),
            organizer = user,
            eventStatus = EventStatus.PUBLISHED
        )
    }

    "toEntity маппит dto в entity" {
        val dto = EventCreateRequest(
            title = "Event Title",
            description = "Event Description",
            dateTime = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
            location = null,
            capacity = 10,
            price = BigDecimal(0),
            eventStatus = EventStatus.PUBLISHED
        )

        val entity = eventMapper.toEntity(dto, user)

        entity.title shouldBe "Event Title"
        entity.description shouldBe "Event Description"
        entity.dateTime shouldBe LocalDateTime.of(2021, 1, 1, 1, 1, 1)
        entity.location shouldBe null
        entity.capacity shouldBe 10
        entity.price shouldBe BigDecimal(0)
        entity.eventStatus shouldBe EventStatus.PUBLISHED
    }

    "updateEntity обновляет entity выборочно" {
        val dto = EventUpdateRequest(
            title = "New Event Title",
            description = "New Event Description",
            dateTime = LocalDateTime.of(2022, 1, 1, 1, 1, 1),
            eventStatus = EventStatus.DRAFT,
            location = null,
            capacity = null,
            price = null
        )

        eventMapper.updateEntity(dto, event)

        event.title shouldBe "New Event Title"
        event.description shouldBe "New Event Description"
        event.dateTime shouldBe LocalDateTime.of(2022, 1, 1, 1, 1, 1)
        event.location shouldBe null
        event.price shouldBe BigDecimal(0)
        event.capacity shouldBe 1
        event.eventStatus shouldBe EventStatus.DRAFT
    }

    "toLongDto маппит entity в longDto" {
        val userDto = UserResponseShort("username")
        val locationDto = null

        every { userMapper.toShortDto(user) } returns userDto
        every { locationMapper.toLongDto(null) } returns locationDto

        val dto = eventMapper.toLongDto(event)

        dto.id shouldBe 1
        dto.title shouldBe "Event Title"
        dto.dateTime shouldBe LocalDateTime.of(2021, 1, 1, 1, 1, 1)
        dto.location shouldBe null
        dto.capacity shouldBe 1
        dto.price shouldBe BigDecimal(0)
        dto.eventStatus shouldBe EventStatus.PUBLISHED
        dto.organizer shouldBe userDto
        dto.location shouldBe locationDto
    }

    "toShortDto маппит entity в shortDto" {
        val dto = eventMapper.toShortDto(event)

        dto.id shouldBe 1
        dto.title shouldBe "Event Title"
        dto.dateTime shouldBe LocalDateTime.of(2021, 1, 1, 1, 1, 1)
    }
})