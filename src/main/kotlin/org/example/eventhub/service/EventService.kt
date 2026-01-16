package org.example.eventhub.service

import org.example.eventhub.dto.event.*
import org.example.eventhub.entity.Event
import org.example.eventhub.exception.EventNotFoundException
import org.example.eventhub.exception.NoAccessException
import org.example.eventhub.mapper.EventMapper
import org.example.eventhub.repository.EventRepository
import org.example.eventhub.specification.EventSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
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

    fun createEvent(dto: EventCreateRequest, userDetails: UserDetails): EventResponseLong {
        val user = userService.getUserByEmailAsEntity(userDetails.username)
        val event = mapper.toEntity(dto, user)
        return mapper.toLongDto(repository.save(event))
    }

    fun getEventById(eventId: Long): EventResponseLong =
        mapper.toLongDto(getEventByIdAsEntity(eventId))

    fun getAllEvents(pageable: Pageable, eventFilter: EventFilter): Page<EventResponseShort> =
        repository.findAll(specification.withFilter(eventFilter), pageable)
            .map(mapper::toShortDto)

    fun updateEvent(userDetails: UserDetails, eventId: Long, dto: EventUpdateRequest): EventResponseLong {
        val event = getEventByIdAsEntity(eventId)

        checkAccess(userDetails, event)

        mapper.updateEntity(dto, event)
        return mapper.toLongDto(event)
    }

    fun deleteEvent(userDetails: UserDetails, eventId: Long) {
        val event = getEventByIdAsEntity(eventId)

        checkAccess(userDetails, event)

        repository.deleteById(eventId)
    }

    fun getEventByIdAsEntity(eventId: Long): Event =
        repository.findById(eventId)
            .orElseThrow { EventNotFoundException("Event с id $eventId не найден") }

    fun saveEvent(event: Event) =
        repository.save(event)

    private fun checkAccess(userDetails: UserDetails, event: Event) {
        val user = userService.getUserByEmailAsEntity(userDetails.username)

        if (event.organizer != user)
            throw NoAccessException(
                "Вы не можете изменять мероприятие ${event.title}, так как не являетесь его организатором"
            )
    }
}
