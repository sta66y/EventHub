package org.example.eventhub.mapper

import org.example.eventhub.dto.location.LocationCreateRequest
import org.example.eventhub.dto.location.LocationResponseLong
import org.example.eventhub.dto.location.LocationUpdateRequest
import org.example.eventhub.entity.Location
import org.springframework.stereotype.Component

@Component
class LocationMapper {

    fun toEntity(dto: LocationCreateRequest): Location =
        Location(dto.city, dto.street, dto.house, dto.additionalInfo)

    fun toEntity(dto: LocationUpdateRequest): Location =
        Location(dto.city, dto.street, dto.house, dto.additionalInfo)

    fun toLongDto(entity: Location?): LocationResponseLong? =
        _root_ide_package_.org.example.eventhub.dto.location.LocationResponseLong(
            entity?.city, entity?.street, entity?.house, entity?.additionalInfo
        )
}
