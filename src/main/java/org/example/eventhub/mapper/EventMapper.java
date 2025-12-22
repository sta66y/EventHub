package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.event.EventCreateRequest;
import org.example.eventhub.dto.event.EventResponseLong;
import org.example.eventhub.dto.event.EventResponseShort;
import org.example.eventhub.dto.event.EventUpdateRequest;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Location;
import org.springframework.stereotype.Component;
import org.example.eventhub.entity.User;


@Component
@RequiredArgsConstructor
public class EventMapper {

    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    public Event toEntity(EventCreateRequest dto, User user) {
        Event event = Event.builder()
                .title(dto.title())
                .dateTime(dto.dateTime())
                .capacity(dto.capacity())
                .organizer(user)
                .build();

        if (dto.description() != null) event.setDescription(dto.description());
        if (dto.location() != null) event.setLocation(locationMapper.toEntity(dto.location()));
        if (dto.eventStatus() != null) event.setEventStatus(dto.eventStatus());
        if (dto.price() != null) event.setPrice(dto.price());

        return event;
    }

    public void updateEntity(EventUpdateRequest dto, Event event) {
        if (dto.title() != null) event.setTitle(dto.title());
        if (dto.description() != null) event.setDescription(dto.description());
        if (dto.dateTime() != null) event.setDateTime(dto.dateTime());
        if (dto.location() != null) {
            Location location = event.getLocation();
            if (dto.location().city() != null) location.setCity(dto.location().city());
            if (dto.location().street() != null) location.setStreet(dto.location().street());
            if (dto.location().house() != null) location.setHouse(dto.location().house());
            if (dto.location().additionalInfo() != null) location.setAdditionalInfo(dto.location().additionalInfo());
            event.setLocation(location);
        }
        if (dto.capacity() != null) event.setCapacity(dto.capacity());
        if (dto.price() != null) event.setPrice(dto.price());
        if (dto.eventStatus() != null) event.setEventStatus(dto.eventStatus());
    }

    public EventResponseLong toLongDto(Event entity) {
        return new EventResponseLong(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDateTime(),
                locationMapper.toLongDto(entity.getLocation()),
                entity.getCapacity(),
                entity.getPrice(),
                entity.getEventStatus(),
                userMapper.toShortDto(entity.getOrganizer())
        );
    }

    public EventResponseShort toShortDto(Event entity) {
        return new EventResponseShort(
                entity.getId(),
                entity.getTitle(),
                entity.getDateTime()
        );
    }
}
