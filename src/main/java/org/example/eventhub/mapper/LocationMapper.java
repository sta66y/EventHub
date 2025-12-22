package org.example.eventhub.mapper;

import org.example.eventhub.dto.location.LocationCreateRequest;
import org.example.eventhub.dto.location.LocationResponseLong;
import org.example.eventhub.dto.location.LocationUpdateRequest;
import org.example.eventhub.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(LocationCreateRequest dto) {
        return new Location(
                dto.city(),
                dto.street(),
                dto.house(),
                dto.additionalInfo()
        );
    }

    public LocationResponseLong toLongDto(Location entity) {
        return new LocationResponseLong(
                entity.getCity(),
                entity.getStreet(),
                entity.getHouse(),
                entity.getAdditionalInfo()
        );
    }
}
