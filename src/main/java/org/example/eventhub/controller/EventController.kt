package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.event.*
import org.example.eventhub.service.EventService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val service: EventService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllEvents(
        pageable: Pageable,
        eventFilter: EventFilter
    ): Page<EventResponseShort> =
        service.getAllEvents(pageable, eventFilter)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createEvent(
        @Valid @RequestBody dto: EventCreateRequest,
        @RequestParam organizerId: Long
    ): EventResponseLong =
        service.createEvent(dto, organizerId)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getEventById(@PathVariable id: Long): EventResponseLong =
        service.getEventById(id)

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateEvent(
        @PathVariable id: Long,
        @Valid @RequestBody dto: EventUpdateRequest
    ): EventResponseLong =
        service.updateEvent(id, dto)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteEvent(@PathVariable id: Long) {
        service.deleteEvent(id)
    }
}
