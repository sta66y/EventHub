package org.example.eventhub.service;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.event.EventUpdateRequest;
import org.example.eventhub.entity.Location;
import org.example.eventhub.exception.EventNotFoundException;
import org.example.eventhub.dto.event.EventCreateRequest;
import org.example.eventhub.dto.event.EventResponseLong;
import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.mapper.EventMapper;
import org.example.eventhub.mapper.LocationMapper;
import org.example.eventhub.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.example.eventhub.entity.User;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repository;
    private final UserService userService;
    private final EventMapper mapper;

    private final LocationMapper locationMapper;

    public EventResponseLong createEvent(EventCreateRequest dto, Long organizerId) {
        User user = userService.getUserByIdAsEntity(organizerId);
        Event event = mapper.toEntity(dto, user);

        return mapper.toLongDto(repository.save(event));
    }

    public EventResponseLong getEventById(Long id) {
        return mapper.toLongDto(getEventByIdAsEntity(id));
    }

    public Page<EventResponseShort> getAllEvents(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toShortDto);
    }

    public EventResponseLong updateEvent(Long id, EventUpdateRequest dto) {
        Event event = getEventByIdAsEntity(id);

        mapper.updateEntity(dto, event);

        return mapper.toLongDto(repository.save(event));
    }

    public void deleteEvent(Long id) {
        getEventByIdAsEntity(id);

        repository.deleteById(id);
    }


    public Event getEventByIdAsEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new EventNotFoundException("Event с id " + id + " не найден"));
    }

//    public List<EventResponseShort> getEventsByFilter(
//            String title, String city, LocalDateTime dateTime, Integer price) TODO
}
