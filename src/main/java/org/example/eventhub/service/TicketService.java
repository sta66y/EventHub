package org.example.eventhub.service;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.entity.User;
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

        checkIfThereAnyAvailableTickets(event);

        return Ticket.builder()
                .order(order)
                .event(event)
                .user(user)
                .price(event.getPrice())
                .build();
    }

    private void checkIfThereAnyAvailableTickets(Event event) {
        if (event.getCapacity() <= repository.countActiveByEvent(event))
            throw new NoAvailableTicketsException(
                    "Все билеты на мероприятие с id " + event.getId() + " уже распроданы");
    }
}
