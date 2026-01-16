package org.example.eventhub.mapper

import org.example.eventhub.dto.event.EventCreateRequest
import org.example.eventhub.dto.event.EventResponseLong
import org.example.eventhub.dto.event.EventResponseShort
import org.example.eventhub.dto.event.EventUpdateRequest
import org.example.eventhub.entity.Event
import org.example.eventhub.entity.User
import org.springframework.stereotype.Component

@Component
class EventMapper(
    private val userMapper: UserMapper,
    private val locationMapper: LocationMapper
) {

    fun toEntity(dto: EventCreateRequest, user: User): Event =
        Event(
            title = dto.title,
            dateTime = dto.dateTime,
            capacity = dto.capacity,
            organizer = user
        ).apply {
            dto.description?.let { description = it }
            dto.location?.let { location = locationMapper.toEntity(it) }
            dto.eventStatus?.let { eventStatus = it }
            dto.price?.let { price = it }
        }

    fun updateEntity(dto: EventUpdateRequest, event: Event) =
        event.apply {
            dto.title?.let { title = it }
            dto.description?.let { description = it }
            dto.dateTime?.let { dateTime = it }

            dto.location?.let { locDto ->
                val loc = location
                if (loc != null) {
                    locDto.city?.let { loc.city = it }
                    locDto.street?.let { loc.street = it }
                    locDto.house?.let { loc.house = it }
                    locDto.additionalInfo?.let { loc.additionalInfo = it }
                } else {
                    location = locationMapper.toEntity(locDto)
                }
            }

            dto.capacity?.let { capacity = it }
            dto.price?.let { price = it }
            dto.eventStatus?.let { eventStatus = it }
        }

    fun toLongDto(entity: Event): EventResponseLong =
        EventResponseLong(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            dateTime = entity.dateTime,
            location = locationMapper.toLongDto(entity.location),
            capacity = entity.capacity,
            price = entity.price,
            eventStatus = entity.eventStatus,
            organizer = userMapper.toShortDto(entity.organizer)
        )

    fun toShortDto(entity: Event): EventResponseShort =
        EventResponseShort(
            id = entity.id,
            title = entity.title,
            dateTime = entity.dateTime
        )
}