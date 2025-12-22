package org.example.eventhub.service;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.ticket.TicketResponseLong;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.example.eventhub.entity.Ticket;
import org.example.eventhub.enums.TicketStatus;
import org.example.eventhub.exception.NoAvailableTicketsException;
import org.example.eventhub.exception.TicketNotFoundException;
import org.example.eventhub.mapper.TicketMapper;
import org.example.eventhub.repository.TicketRepository;
import org.springframework.stereotype.Service;

import org.example.eventhub.entity.User;


@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository repository;
    private final UserService userService;
    private final EventService eventService;
    private final TicketMapper mapper;

    public Ticket createTicket(Long eventId, Long userId, Order order) {
        User user = userService.getUserByIdAsEntity(userId);
        Event event = eventService.getEventByIdAsEntity(eventId);

        checkIfThereAnyAvailableTickets(event);

        Ticket ticket = Ticket.builder()
                .order(order)
                .event(event)
                .user(user)
                .price(event.getPrice())
                .build();

        return ticket;
    }

    public TicketResponseLong returnTicket(Long ticketId, Long userId) {
        User user = userService.getUserByIdAsEntity(userId);

        Ticket ticket = getTicketByIdAsEntity(ticketId);

        ticket.setStatus(TicketStatus.CANCELLED);

        return mapper.toLongDto(repository.save(ticket));
    }

    private Ticket getTicketByIdAsEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Билет с id " + id + " не найден"));
    }

    private void checkIfThereAnyAvailableTickets(Event event) {
        if (event.getCapacity() < repository.countActiveByEvent(event))
            throw new NoAvailableTicketsException("Все билеты на мероприятие с id " + event.getId() + " уже распроданы");
    }
}
