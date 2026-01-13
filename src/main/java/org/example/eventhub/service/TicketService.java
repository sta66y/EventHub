package org.example.eventhub.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.TicketStatus;
import org.example.eventhub.exception.NoAvailableTicketsException;
import org.example.eventhub.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
class TicketService {

    private final TicketRepository repository;
    private final UserService userService;
    private final EventService eventService;

    Ticket createTicket(Long eventId, Long userId, Order order) {
        User user = userService.getUserByIdAsEntity(userId);
        Event event = eventService.getEventByIdAsEntity(eventId);

        event.incrementReservedCount();

        try {
            eventService.saveEvent(event);
        } catch (OptimisticLockException e) {
            throw new NoAvailableTicketsException("Билеты только что закончились");
        }


        return repository.save(
                Ticket.builder()
                    .order(order)
                    .event(event)
                    .user(user)
                    .price(event.getPrice())
                    .reservedUntil(LocalDateTime.now().plusMinutes(15)) //TODO вынести
                    .build()
        );
    }

    List<Ticket> findExpiredReserved(LocalDateTime now) {
        return repository.findTicketsByStatusAndReservedUntilBefore(TicketStatus.RESERVED, now);
    }
}
