package org.example.eventhub.service;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.TicketStatus;
import org.example.eventhub.exception.NoAvailableTicketsException;
import org.example.eventhub.repository.TicketRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository repository;
    private final UserService userService;
    private final EventService eventService;

    public Ticket createTicket(Long eventId, Long userId, Order order) {
        User user = userService.getUserByIdAsEntity(userId);
        Event event = eventService.getEventByIdAsEntity(eventId);

        if (event.getReservedCount() >= event.getCapacity()) {
            throw new NoAvailableTicketsException("Свободных билетов для " + event.getTitle() + " не осталось");
        }

        event.setReservedCount(event.getReservedCount() + 1);
        eventService.saveEvent(event);


        return repository.save(
                Ticket.builder()
                    .order(order)
                    .event(event)
                    .user(user)
                    .price(event.getPrice())
                    .build()
        );
    }
}
