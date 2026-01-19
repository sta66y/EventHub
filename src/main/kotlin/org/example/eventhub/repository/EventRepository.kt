package org.example.eventhub.repository

import org.example.eventhub.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface EventRepository : JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>
