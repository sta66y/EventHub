package org.example.eventhub.repository

import org.example.eventhub.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, Long>
