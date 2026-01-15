package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.event.*
import org.example.eventhub.service.EventService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val service: EventService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllEvents(pageable: Pageable, eventFilter: EventFilter): Page<EventResponseShort> =
        service.getAllEvents(pageable, eventFilter)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createEvent(
        @Valid @RequestBody dto: EventCreateRequest,
        @AuthenticationPrincipal user: UserDetails
    ): EventResponseLong =
        service.createEvent(dto, user)

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    fun getEventById(@PathVariable eventId: Long): EventResponseLong =
        service.getEventById(eventId)

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateEvent(
        @AuthenticationPrincipal user: UserDetails,
        @PathVariable eventId: Long,
        @Valid @RequestBody dto: EventUpdateRequest
    ): EventResponseLong =
        service.updateEvent(user, eventId, dto)

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteEvent(@AuthenticationPrincipal user: UserDetails, @PathVariable eventId: Long) =
        service.deleteEvent(user, eventId)
}
