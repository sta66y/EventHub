package org.example.eventhub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.event.*;
import org.example.eventhub.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EventResponseShort> getAllEvents(Pageable pageable, EventFilter eventFilter) {
        return service.getAllEvents(pageable, eventFilter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseLong createEvent(@Valid @RequestBody EventCreateRequest dto, @RequestParam Long organizerId) {
        return service.createEvent(dto, organizerId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseLong getEventById(@PathVariable Long id) {
        return service.getEventById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseLong updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateRequest dto) {
        return service.updateEvent(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
    }
}
