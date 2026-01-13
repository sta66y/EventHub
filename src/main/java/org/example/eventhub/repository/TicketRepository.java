package org.example.eventhub.repository;

import org.example.eventhub.entity.Ticket;
import org.example.eventhub.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findTicketsByStatusAndReservedUntilBefore(TicketStatus status, LocalDateTime now);
}
