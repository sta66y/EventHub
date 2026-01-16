package org.example.eventhub.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.eventhub.entity.Event
import org.example.eventhub.entity.Order
import org.example.eventhub.entity.Ticket
import org.example.eventhub.entity.User
import org.example.eventhub.enums.OrderStatus
import org.example.eventhub.enums.Role
import org.example.eventhub.exception.NoAvailableTicketsException
import org.example.eventhub.repository.TicketRepository
import java.math.BigDecimal
import java.time.LocalDateTime

class TicketServiceTest : StringSpec({

    val repository = mockk<TicketRepository>()
    val eventService = mockk<EventService>()

    val service = TicketService(repository, eventService)

    val user = User(
        id = 1,
        username = "username",
        email = "email@email.com",
        password = "password",
        role = Role.USER
    )
    val order = Order(
        id = 1,
        user = user,
        totalPrice = BigDecimal.ZERO,
        createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
        status = OrderStatus.PENDING,
        reservedUntil = LocalDateTime.of(2021, 1, 1, 1, 16, 0)
    )

    "createTicket создает билет, учитывает это в event, сохраняет в репозиторий" {
        val event = Event(
            id = 1,
            title = "title",
            dateTime = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
            capacity = 1,
            organizer = user
        )
        val ticket = Ticket(
            id = 1,
            event = event,
            user = user,
            order = order,
            price = BigDecimal.ZERO,
        )

        every { eventService.getEventByIdAsEntity(any()) } returns event
        every { eventService.saveEvent(any()) } returns event
        every { repository.save(any()) } returns ticket

        service.createTicket(1, user, order)

        verify(exactly = 1) { repository.save(any()) }
        verify(exactly = 1) { eventService.saveEvent(any()) }
        verify(exactly = 1) { eventService.getEventByIdAsEntity(any()) }
    }

    "createTicket выбрасывает ошибку, если билетов на мероприятие больше нет" {
        val event = Event(
            id = 1,
            title = "title",
            dateTime = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
            capacity = 0,
            organizer = user
        )

        every { eventService.getEventByIdAsEntity(any()) } returns event

        val ex = shouldThrow<NoAvailableTicketsException> {
            service.createTicket(1, user, order)
        }

        ex.message shouldBe "Свободных билетов для ${event.title} не осталось"
    }
})