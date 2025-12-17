package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.EventDtoRequest;
import org.example.eventhub.dto.EventResponseLong;
import org.example.eventhub.dto.EventResponseShort;
import org.example.eventhub.entity.Event;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final UserMapper userMapper;

    public Event toEntity(EventDtoRequest dto) {
        return new Event().builder()
                .title(dto.title())
                .description(dto.description())
                .dateTime(dto.dateTime())
                .location(dto.location())
                .capacity(dto.capacity())
                .price(dto.price())
                .eventStatus(dto.eventStatus())
                .build();
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
