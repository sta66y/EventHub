package org.example.eventhub.service

import org.example.eventhub.dto.event.*
import org.example.eventhub.entity.Event
import org.example.eventhub.exception.EventNotFoundException
import org.example.eventhub.mapper.EventMapper
import org.example.eventhub.repository.EventRepository
import org.example.eventhub.specification.EventSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EventService(
    private val repository: EventRepository,
    private val userService: UserService,
    private val mapper: EventMapper,
    private val specification: EventSpecification
) {

    fun createEvent(
        dto: EventCreateRequest,
        organizerId: Long
    ): EventResponseLong {
        val user = userService.getUserByIdAsEntity(organizerId)
        val event = mapper.toEntity(dto, user)
        return mapper.toLongDto(repository.save(event))
    }

    fun getEventById(id: Long): EventResponseLong =
        mapper.toLongDto(getEventByIdAsEntity(id))

    fun getAllEvents(
        pageable: Pageable,
        eventFilter: EventFilter
    ): Page<EventResponseShort> =
        repository.findAll(specification.withFilter(eventFilter), pageable)
            .map(mapper::toShortDto)

    fun updateEvent(
        id: Long,
        dto: EventUpdateRequest
    ): EventResponseLong {
        val event = getEventByIdAsEntity(id)
        mapper.updateEntity(dto, event)
        return mapper.toLongDto(event)
    }

    fun deleteEvent(id: Long) {
        getEventByIdAsEntity(id)
        repository.deleteById(id)
    }

    fun getEventByIdAsEntity(id: Long): Event =
        repository.findById(id)
            .orElseThrow { EventNotFoundException("Event с id $id не найден") }

    fun saveEvent(event: Event) = repository.save(event)
}
