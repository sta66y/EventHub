package org.example.eventhub.service

import org.example.eventhub.dto.order.OrderCreateRequest
import org.example.eventhub.dto.order.OrderResponseLong
import org.example.eventhub.dto.order.OrderResponseShort
import org.example.eventhub.entity.Order
import org.example.eventhub.entity.Ticket
import org.example.eventhub.enums.OrderStatus
import org.example.eventhub.enums.TicketStatus
import org.example.eventhub.exception.OrderNotFoundException
import org.example.eventhub.mapper.OrderMapper
import org.example.eventhub.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
open class OrderService(
    private val userService: UserService,
    private val ticketService: TicketService,
    private val mapper: OrderMapper,
    private val repository: OrderRepository
) {

    fun createOrder(dto: OrderCreateRequest, userId: Long): OrderResponseLong {
        val user = userService.getUserByIdAsEntity(userId)

        val order = Order(
            user = user,
            reservedUntil = LocalDateTime.now().plusMinutes(15),
            totalPrice = BigDecimal.ZERO,
        )

        reserveTickets(order, dto.eventsId, userId)

        return mapper.toLongDto(repository.save(order))
    }

    @Scheduled(cron = "0 * * * * *")
    fun cancelExpiredReservations() {
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

    fun payOrder(orderId: Long) {
        val order = getOrderByIdAsEntity(orderId)

        if (order.status != OrderStatus.PENDING) {
            error("Нельзя оплатить заказ в статусе ${order.status}")
        }

        order.status = OrderStatus.PAID
        order.tickets.forEach { it.status = TicketStatus.PAID }
    }

    fun cancelOrder(orderId: Long) {
        val order = getOrderByIdAsEntity(orderId)

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
        userId: Long
    ) {
        var totalPrice = BigDecimal.ZERO
        val tickets = mutableListOf<Ticket>()

        eventIds.forEach { eventId ->
            val ticket = ticketService.createTicket(eventId, userId, order)
            tickets += ticket
            totalPrice += ticket.price
        }

        order.totalPrice = totalPrice
        order.tickets = tickets
    }

    fun getOrderById(id: Long): OrderResponseLong =
        mapper.toLongDto(getOrderByIdAsEntity(id))

    fun getAllOrders(userId: Long, pageable: Pageable): Page<OrderResponseShort> =
        repository.findAllByUserId(userId, pageable)
            .map(mapper::toShortDto)

    private fun getOrderByIdAsEntity(id: Long): Order =
        repository.findById(id)
            .orElseThrow { OrderNotFoundException("Ордера с id $id не существует") }
}
