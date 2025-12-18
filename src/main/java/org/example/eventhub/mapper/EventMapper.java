package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.EventCreateRequest;
import org.example.eventhub.dto.EventResponseLong;
import org.example.eventhub.dto.EventResponseShort;
import org.example.eventhub.dto.EventUpdateRequest;
import org.example.eventhub.entity.Event;
import org.springframework.stereotype.Component;
import org.example.eventhub.entity.User;


@Component
@RequiredArgsConstructor
public class EventMapper {

    private final UserMapper userMapper;

    public Event toEntity(EventCreateRequest dto, User organizer) {
        Event event = Event.builder()
                .title(dto.title())
                .dateTime(dto.dateTime())
                .capacity(dto.capacity())
                .organizer(organizer)
                .build();

        if (dto.description() != null) event.setDescription(dto.description());
        if (dto.location() != null) event.setLocation(dto.location());
        if (dto.eventStatus() != null) event.setEventStatus(dto.eventStatus());
        if (dto.price() != null) event.setPrice(dto.price());

        return event;
    }

    public EventResponseLong toLongDto(Event entity) {
        return new EventResponseLong(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDateTime(),
                entity.getLocation(),
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
