package org.example.eventhub.repository;

import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
