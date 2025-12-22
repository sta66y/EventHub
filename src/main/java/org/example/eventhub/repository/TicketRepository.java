package org.example.eventhub.repository;

import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event = :event AND t.status IN ('PAID', 'RESERVED')")
    int countActiveByEvent(@Param("event") Event event);
}
