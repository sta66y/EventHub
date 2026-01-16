package org.example.eventhub.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.example.eventhub.dto.order.OrderCreateRequest
import org.example.eventhub.dto.order.OrderResponseLong
import org.example.eventhub.dto.order.OrderResponseShort
import org.example.eventhub.entity.Event
import org.example.eventhub.entity.Order
import org.example.eventhub.entity.Ticket
import org.example.eventhub.entity.User
import org.example.eventhub.enums.OrderStatus
import org.example.eventhub.enums.Role
import org.example.eventhub.enums.TicketStatus
import org.example.eventhub.exception.NoAccessException
import org.example.eventhub.exception.OrderNotFoundException
import org.example.eventhub.mapper.OrderMapper
import org.example.eventhub.repository.OrderRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

class OrderServiceTest : StringSpec({

    val userService = mockk<UserService>()
    val ticketService = mockk<TicketService>()
    val mapper = mockk<OrderMapper>()
    val repository = mockk<OrderRepository>()

    val service = OrderService(userService, ticketService, mapper, repository)

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

    //------------createOrder------------

    "createOrder создает заказ и резервирует билеты" {
        val dto = OrderCreateRequest(
            eventsId = listOf(1, 2)
        )

        val orderSlot = slot<Order>()
        val ticket1 = mockk<Ticket> {
            every { price } returns BigDecimal.TEN
        }
        val ticket2 = mockk<Ticket> {
            every { price } returns BigDecimal.ONE
        }

        every { ticketService.createTicket(1, user, any()) } returns ticket1
        every { ticketService.createTicket(2, user, any()) } returns ticket2

        every { repository.save(capture(orderSlot)) } answers { orderSlot.captured }

        val response = mockk<OrderResponseLong>()
        every { mapper.toLongDto(any()) } returns response

        val result = service.createOrder(dto, userDetails)

        result shouldBe response
        orderSlot.captured.totalPrice shouldBe BigDecimal.valueOf(11)
        orderSlot.captured.tickets.size shouldBe 2
    }

    //-------------getOrderById-----------

    "getOrderById возвращает заказ в LongDto" {
        val order = Order(
            id = 1,
            user = user,
            totalPrice = BigDecimal.ZERO,
            createdAt = LocalDateTime.now(),
            status = OrderStatus.PENDING,
            reservedUntil = LocalDateTime.now().plusMinutes(15),
        )

        every { repository.findById(1) } returns Optional.of(order)

        val response = mockk<OrderResponseLong>()
        every { mapper.toLongDto(order) } returns response

        service.getOrderById(1) shouldBe response
    }

    "getOrderById выбрасывает ошибку если заказ не найден" {
        every { repository.findById(1) } returns Optional.empty()

        val ex = shouldThrow<OrderNotFoundException> {
            service.getOrderById(1)
        }

        ex.message shouldBe "Заказа с id 1 не существует"
    }

    //-----------getAllOrders-------------

    "getAllOrders возвращает заказы пользователя" {
        val pageable = mockk<Pageable>()
        val order = mockk<Order>()

        val page = PageImpl(listOf(order))
        every { repository.findAllByUserId(1, pageable) } returns page

        val short = mockk<OrderResponseShort>()
        every { mapper.toShortDto(order) } returns short

        val result = service.getAllOrders(userDetails, pageable)

        result.content shouldBe listOf(short)
    }

    //----------payOrder-----------

    "payOrder переводит заказ в PAID" {
        val ticket = mockk<Ticket>(relaxed = true)
        val order = Order(
            id = 1,
            user = user,
            status = OrderStatus.PENDING,
            tickets = mutableListOf(ticket),
            totalPrice = BigDecimal.ZERO,
            createdAt = LocalDateTime.now(),
            reservedUntil = LocalDateTime.now().plusMinutes(15)
        )

        every { repository.findById(1) } returns Optional.of(order)

        service.payOrder(userDetails, 1)

        order.status shouldBe OrderStatus.PAID
        verify { ticket.status = TicketStatus.PAID }
    }

    "payOrder выбрасывает NoAccessException, если нет доступа" {
        val otherUser = User(
            id = 2,
            username = "other",
            email = "other@email.com",
            password = "password",
            role = Role.USER
        )


        val order = Order(
            id = 1,
            user = otherUser,
            status = OrderStatus.PENDING,
            totalPrice = BigDecimal.ZERO,
            createdAt = LocalDateTime.now(),
            reservedUntil = LocalDateTime.now().plusMinutes(15)
        )

        every { repository.findById(1) } returns Optional.of(order)

        shouldThrow<NoAccessException> {
            service.payOrder(userDetails, 1)
        }
    }

    //----------cancelOrder------------

    "cancelOrder отменяет заказ и освобождает билеты" {
        val event = mockk<Event>(relaxed = true)
        val order = Order(
            id = 1,
            user = user,
            status = OrderStatus.PENDING,
            tickets = mutableListOf(),
            totalPrice = BigDecimal.ZERO,
            createdAt = LocalDateTime.now(),
            reservedUntil = LocalDateTime.now().plusMinutes(15)
        )

        val ticket = Ticket(
            event = event,
            user = user,
            order = order,
            price = BigDecimal.ZERO,
            status = TicketStatus.RESERVED
        )

        order.tickets.add(ticket)

        every { repository.findById(1) } returns Optional.of(order)

        service.cancelOrder(userDetails, 1)

        order.status shouldBe OrderStatus.CANCELLED
        ticket.status shouldBe TicketStatus.CANCELLED
        verify { event.decrementReservedCount() }
    }

    "cancelExpiredReservations отменяет просроченные заказы" {
        val event = mockk<Event>(relaxed = true)
        val order = Order(
            id = 1,
            user = user,
            status = OrderStatus.PENDING,
            tickets = mutableListOf(),
            totalPrice = BigDecimal.ZERO,
            createdAt = LocalDateTime.now(),
            reservedUntil = LocalDateTime.now().plusMinutes(15)
        )

        val ticket = Ticket(
            event = event,
            user = user,
            order = order,
            price = BigDecimal.ZERO,
            status = TicketStatus.RESERVED
        )

        order.tickets.add(ticket)

        every {
            repository.findOrdersByStatusAndReservedUntilBefore(
                OrderStatus.PENDING,
                any()
            )
        } returns listOf(order)

        service.cancelExpiredReservations()

        order.status shouldBe OrderStatus.CANCELLED
        ticket.status shouldBe TicketStatus.CANCELLED
        verify { event.decrementReservedCount() }
    }
})