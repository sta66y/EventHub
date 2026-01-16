package org.example.eventhub.mapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.example.eventhub.dto.event.EventResponseShort
import org.example.eventhub.dto.ticket.TicketResponseShort
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.entity.*
import org.example.eventhub.enums.*
import java.math.BigDecimal
import java.time.LocalDateTime

class OrderMapperTest : StringSpec({

    val userMapper = mockk<UserMapper>()
    val ticketMapper = mockk<TicketMapper>()

    val mapper = OrderMapper(userMapper, ticketMapper)

    val userDto = UserResponseShort("username")
    val ticketDto = TicketResponseShort(
        EventResponseShort(null, "title", LocalDateTime.of(2019, 12, 22, 0, 0)),
        userDto,
        TicketStatus.RESERVED
    )

    lateinit var order: Order
    lateinit var user: User
    lateinit var ticket: Ticket

    beforeEach {
        user = User(
            id = 1L,
            username = "user",
            email = "mail@mail.com",
            password = "pass"
        )

        ticket = mockk()

        order = Order(
            id = 10L,
            user = user,
            tickets = mutableListOf(ticket),
            totalPrice = BigDecimal.TEN,
            status = OrderStatus.PAID,
            reservedUntil = LocalDateTime.now().plusMinutes(15),
            createdAt = LocalDateTime.now()
        )
    }

    "toShortDto маппит order в short dto" {
        every { ticketMapper.toShortDto(ticket) } returns ticketDto

        val dto = mapper.toShortDto(order)

        dto.tickets shouldBe listOf(ticketDto)
        dto.totalPrice shouldBe BigDecimal.TEN
        dto.orderStatus shouldBe OrderStatus.PAID
    }

    "toLongDto маппит order в long dto" {
        every { ticketMapper.toShortDto(ticket) } returns ticketDto
        every { userMapper.toShortDto(user) } returns userDto

        val dto = mapper.toLongDto(order)

        dto.id shouldBe 10L
        dto.user shouldBe userDto
        dto.tickets shouldBe listOf(ticketDto)
        dto.totalPrice shouldBe BigDecimal.TEN
        dto.orderStatus shouldBe OrderStatus.PAID
    }
})
