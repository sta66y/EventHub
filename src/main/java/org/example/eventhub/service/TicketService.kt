package org.example.eventhub.service

import jakarta.persistence.OptimisticLockException
import org.example.eventhub.entity.Order
import org.example.eventhub.entity.Ticket
import org.example.eventhub.exception.NoAvailableTicketsException
import org.example.eventhub.repository.TicketRepository
import org.springframework.stereotype.Service

import org.example.eventhub.entity.User


@Service
class TicketService(
    private val repository: TicketRepository,
    private val eventService: EventService
) {

    fun createTicket(eventId: Long, user: User, order: Order): Ticket {
        val event = eventService.getEventByIdAsEntity(eventId)

        event.incrementReservedCount()

        try {
            eventService.saveEvent(event)
        } catch (e: OptimisticLockException) {
            throw NoAvailableTicketsException(
                "Все билеты на мероприятие с id $eventId уже распроданы"
            )
        }

        return repository.save(
            Ticket(
                order = order,
                event = event,
                user = user,
                price = event.price
            )
        )
    }
}
