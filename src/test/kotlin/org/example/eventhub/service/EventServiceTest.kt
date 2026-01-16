package org.example.eventhub.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.example.eventhub.dto.event.*
import org.example.eventhub.entity.Event
import org.example.eventhub.entity.User
import org.example.eventhub.enums.EventStatus
import org.example.eventhub.enums.Role
import org.example.eventhub.exception.EventNotFoundException
import org.example.eventhub.exception.NoAccessException
import org.example.eventhub.mapper.EventMapper
import org.example.eventhub.repository.EventRepository
import org.example.eventhub.specification.EventSpecification
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

class EventServiceTest : StringSpec({

    val repository = mockk<EventRepository>()
    val userService = mockk<UserService>()
    val mapper = mockk<EventMapper>()
    val specification = mockk<EventSpecification>()

    val service = EventService(repository, userService, mapper, specification)

    val userDetails = mockk<UserDetails>()
    val user = User(
        id = 1,
        username = "user",
        email = "user@mail.com",
        password = "pass",
        role = Role.USER
    )

    every { userDetails.username } returns "user@mail.com"
    every { userService.getUserByEmailAsEntity("user@mail.com") } returns user

    // ---------- createEvent ----------

    "createEvent сохраняет событие и возвращает LongDto" {
        val dto = EventCreateRequest(
            title = "Event",
            description = "desc",
            dateTime = LocalDateTime.now(),
            location = null,
            capacity = 100,
            price = BigDecimal.TEN,
            eventStatus = EventStatus.PUBLISHED
        )

        val event = mockk<Event>()
        val response = mockk<EventResponseLong>()

        every { mapper.toEntity(dto, user) } returns event
        every { repository.save(event) } returns event
        every { mapper.toLongDto(event) } returns response

        service.createEvent(dto, userDetails) shouldBe response
    }

    // ---------- getEventById ----------

    "getEventById возвращает событие" {
        val event = mockk<Event>()
        val response = mockk<EventResponseLong>()

        every { repository.findById(1) } returns Optional.of(event)
        every { mapper.toLongDto(event) } returns response

        service.getEventById(1) shouldBe response
    }

    "getEventById выбрасывает EventNotFoundException если события нет" {
        every { repository.findById(1) } returns Optional.empty()

        val ex = shouldThrow<EventNotFoundException> {
            service.getEventById(1)
        }

        ex.message shouldBe "Event с id 1 не найден"
    }

    // ---------- getAllEvents ----------

    "getAllEvents возвращает страницу ShortDto" {
        val pageable = mockk<Pageable>()
        val filter = EventFilter(
            title = null,
            city = null,
            minCapacity = null,
            maxCapacity = null,
            minPrice = null,
            maxPrice = null,
            eventStatus = null,
            fromDateTime = null,
            toDateTime = null,
            upcoming = null
        )

        val spec = mockk<org.springframework.data.jpa.domain.Specification<Event>>()
        val event = mockk<Event>()
        val shortDto = mockk<EventResponseShort>()

        every { specification.withFilter(filter) } returns spec
        every { repository.findAll(spec, pageable) } returns PageImpl(listOf(event))
        every { mapper.toShortDto(event) } returns shortDto

        val result = service.getAllEvents(pageable, filter)

        result.content shouldBe listOf(shortDto)
    }

    // ---------- updateEvent ----------

    "updateEvent обновляет событие если есть доступ" {
        val dto = EventUpdateRequest(
            title = "new title",
            description = null,
            dateTime = null,
            location = null,
            capacity = null,
            price = null,
            eventStatus = null
        )

        val event = mockk<Event>(relaxed = true)

        every { event.organizer } returns user
        every { repository.findById(1) } returns Optional.of(event)
        every { mapper.updateEntity(dto, event) } returns event

        val response = mockk<EventResponseLong>()
        every { mapper.toLongDto(event) } returns response

        service.updateEvent(userDetails, 1, dto) shouldBe response

        verify { mapper.updateEntity(dto, event) }
    }

    "updateEvent выбрасывает NoAccessException если не организатор" {
        val otherUser = User(
            id = 2,
            username = "other",
            email = "other@mail.com",
            password = "pass",
            role = Role.USER
        )

        val event = mockk<Event>()
        every { event.organizer } returns otherUser
        every { event.title } returns "Event"
        every { repository.findById(1) } returns Optional.of(event)

        shouldThrow<NoAccessException> {
            service.updateEvent(userDetails, 1, mockk())
        }
    }

    // ---------- deleteEvent ----------

    "deleteEvent удаляет событие если есть доступ" {
        val event = mockk<Event>()
        every { event.organizer } returns user
        every { repository.findById(1) } returns Optional.of(event)
        every { repository.deleteById(1) } just Runs

        service.deleteEvent(userDetails, 1)

        verify { repository.deleteById(1) }
    }

    "deleteEvent выбрасывает NoAccessException если нет доступа" {
        val otherUser = User(
            id = 2,
            username = "other",
            email = "other@mail.com",
            password = "pass",
            role = Role.USER
        )

        val event = mockk<Event>()
        every { event.organizer } returns otherUser
        every { event.title } returns "Event"
        every { repository.findById(1) } returns Optional.of(event)

        shouldThrow<NoAccessException> {
            service.deleteEvent(userDetails, 1)
        }
    }
})
