package org.example.eventhub.service

import org.example.eventhub.dto.order.OrderCreateRequest
import org.example.eventhub.dto.order.OrderResponseLong
import org.example.eventhub.dto.order.OrderResponseShort
import org.example.eventhub.entity.User
import org.example.eventhub.entity.Order
import org.example.eventhub.entity.Ticket
import org.example.eventhub.enums.OrderStatus
import org.example.eventhub.enums.TicketStatus
import org.example.eventhub.exception.NoAccessException
import org.example.eventhub.exception.OrderNotFoundException
import org.example.eventhub.mapper.OrderMapper
import org.example.eventhub.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class OrderService(
    private val userService: UserService,
    private val ticketService: TicketService,
    private val mapper: OrderMapper,
    private val repository: OrderRepository
) {

    fun createOrder(dto: OrderCreateRequest, userDetails: UserDetails): OrderResponseLong {
        val user = userService.getUserByEmailAsEntity(userDetails.username)

        val order = Order(
            user = user,
            reservedUntil = LocalDateTime.now().plusMinutes(15),
            totalPrice = BigDecimal.ZERO,
            createdAt = LocalDateTime.now()
        )

        reserveTickets(order, dto.eventsId, user)

        return mapper.toLongDto(repository.save(order))
    }

    @Scheduled(cron = "0 * * * * *")
    fun cancelExpiredReservations()  {
        val expiredOrders =
            repository.findOrdersByStatusAndReservedUntilBefore(
                OrderStatus.PENDING,
                LocalDateTime.now()
            )

        expiredOrders.forEach { order ->
            order.status = OrderStatus.CANCELLED

            order.tickets.forEach { ticket ->
                if (ticket.status == TicketStatus.RESERVED) {
                    ticket.event.decrementReservedCount()
                    ticket.status = TicketStatus.CANCELLED
                }
            }
        }
    }

    fun payOrder(userDetails: UserDetails, orderId: Long) {
        val order = getOrderByIdAsEntity(orderId)

        checkAccess(userDetails, order)

        if (order.status != OrderStatus.PENDING) {
            error("Нельзя оплатить заказ в статусе ${order.status}")
        }

        order.status = OrderStatus.PAID
        order.tickets.forEach { it.status = TicketStatus.PAID }
    }

    fun cancelOrder(userDetails: UserDetails, orderId: Long) {
        val order = getOrderByIdAsEntity(orderId)

        checkAccess(userDetails, order)

        if (order.status != OrderStatus.PENDING) {
            error("Нельзя отменить заказ в статусе ${order.status}")
        }

        order.status = OrderStatus.CANCELLED
        order.tickets.forEach {
            if (it.status != TicketStatus.CANCELLED) {
                it.status = TicketStatus.CANCELLED
                it.event.decrementReservedCount()
            }
        }
    }

    private fun reserveTickets(
        order: Order,
        eventIds: List<Long>,
        user: User
    ) {
        var totalPrice = BigDecimal.ZERO
        val tickets = mutableListOf<Ticket>()

        eventIds.forEach { eventId ->
            val ticket = ticketService.createTicket(eventId, user, order) //TODO тут ошибка вылетает на тесте
            tickets += ticket
            totalPrice = totalPrice.add(ticket.price)
        }

        order.totalPrice = totalPrice
        order.tickets = tickets
    }

    fun getOrderById(orderId: Long): OrderResponseLong =
        mapper.toLongDto(getOrderByIdAsEntity(orderId))

    fun getAllOrders(userDetails: UserDetails, pageable: Pageable): Page<OrderResponseShort> {
        val user = userService.getUserByEmailAsEntity(userDetails.username)

        return repository.findAllByUserId(user.id, pageable)
            .map(mapper::toShortDto)
    }

    private fun getOrderByIdAsEntity(orderId: Long): Order =
        repository.findById(orderId)
            .orElseThrow { OrderNotFoundException("Заказа с id $orderId не существует") }

    private fun checkAccess(userDetails: UserDetails, order: Order) {
        val user = userService.getUserByEmailAsEntity(userDetails.username)

        if (order.user != user)
            throw NoAccessException(
                "Вы не можете взаимодействовать с заказом ${order.id}"
            )
    }
}
