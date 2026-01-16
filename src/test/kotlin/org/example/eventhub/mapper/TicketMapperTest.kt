package org.example.eventhub.mapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.eventhub.dto.event.EventResponseShort
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.entity.*
import org.example.eventhub.enums.*
import java.math.BigDecimal
import java.time.LocalDateTime

class TicketMapperTest : StringSpec({

    val eventMapper = mockk<EventMapper>()
    val userMapper = mockk<UserMapper>()

    val mapper = TicketMapper(eventMapper, userMapper)

    val userDto = UserResponseShort("user")
    val eventDto = EventResponseShort(
        id = 1L,
        title = "Event",
        dateTime = LocalDateTime.of(2021, 1, 1, 0, 0)
    )

    lateinit var user: User
    lateinit var event: Event
    lateinit var order: Order
    lateinit var ticket: Ticket

    beforeEach {
        user = User(
            id = 1L,
            username = "user",
            email = "mail@mail.com",
            password = "pass"
        )

        event = Event(
            id = 1L,
            title = "Event",
            dateTime = LocalDateTime.of(2021, 1, 1, 0, 0),
            capacity = 10,
            price = BigDecimal.TEN,
            organizer = user,
            eventStatus = EventStatus.PUBLISHED
        )

        order = Order(
            id = 100L,
            user = user,
            tickets = mutableListOf(),
            totalPrice = BigDecimal.TEN,
            status = OrderStatus.PAID,
            reservedUntil = LocalDateTime.now().plusMinutes(15)
        )

        ticket = Ticket(
            id = 5L,
            event = event,
            user = user,
            order = order,
            price = BigDecimal.TEN,
            status = TicketStatus.RESERVED
        )
    }

    "toShortDto маппит ticket в short dto" {
        every { eventMapper.toShortDto(event) } returns eventDto
        every { userMapper.toShortDto(user) } returns userDto

        val dto = mapper.toShortDto(ticket)

        dto.event shouldBe eventDto
        dto.user shouldBe userDto
        dto.ticketStatus shouldBe TicketStatus.RESERVED
    }

    "toLongDto маппит ticket в long dto" {
        every { eventMapper.toShortDto(event) } returns eventDto
        every { userMapper.toShortDto(user) } returns userDto

        val dto = mapper.toLongDto(ticket)

        dto.id shouldBe 5L
        dto.event shouldBe eventDto
        dto.user shouldBe userDto
        dto.orderId shouldBe 100L
        dto.price shouldBe BigDecimal.TEN
        dto.ticketStatus shouldBe TicketStatus.RESERVED
    }
})
