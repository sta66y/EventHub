package org.example.eventhub.service;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.event.EventUpdateRequest;
import org.example.eventhub.exception.EventNotFoundException;
import org.example.eventhub.dto.event.EventCreateRequest;
import org.example.eventhub.dto.event.EventResponseLong;
import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.mapper.EventMapper;
import org.example.eventhub.repository.EventRepository;
import org.springframework.stereotype.Service;

import org.example.eventhub.entity.User;


import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repository;
    private final UserService userService;
    private final EventMapper mapper;

    public EventResponseLong createEvent(EventCreateRequest dto, Long organizerId) { // TODO возможно проверку на существование
        User user = userService.getUserByIdAsEntity(organizerId);
        Event event = mapper.toEntity(dto, user);
        Event saved = repository.save(event);

        return mapper.toLongDto(saved);
    }

    public EventResponseLong getEventById(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event с id" + id + " не найдено"));

        return mapper.toLongDto(event);
    }

    public Event getEventByIdAsEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new EventNotFoundException("Event с id " + id + " не найден"));
    }

    public List<EventResponseShort> getAllEvents() { // TODO pageable
        return repository.findAll()
                .stream()
                .map(mapper::toShortDto)
                .toList();
    }

    public EventResponseLong updateEvent(Long id, EventUpdateRequest dto) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event с id" + id + " не найдено"));

        if (dto.title() != null) event.setTitle(dto.title());
        if (dto.description() != null) event.setDescription(dto.description());
        if (dto.dateTime() != null) event.setDateTime(dto.dateTime());
        if (dto.location() != null) event.setLocation(dto.location());
        if (dto.capacity() != null) event.setCapacity(dto.capacity());
        if (dto.price() != null) event.setPrice(dto.price());
        if (dto.eventStatus() != null) event.setEventStatus(dto.eventStatus());

        return mapper.toLongDto(repository.save(event));
    }

    public void deleteEvent(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event с id" + id + " не найдено"));

        repository.deleteById(id);
    }

//    public List<EventResponseShort> getEventsByFilter(
//            String title, String city, LocalDateTime dateTime, Integer price) TODO
}
