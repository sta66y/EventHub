package org.example.eventhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.eventhub.dto.location.LocationCreateRequest;
import org.example.eventhub.dto.location.LocationResponseLong;
import org.example.eventhub.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationMapperTest {

    private final LocationMapper mapper = new LocationMapper();

    private Location entity;
    private LocationCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        entity = new Location("Moscow", "Tverskaya", "10", "near metro");
        createRequest = new LocationCreateRequest("Saint Petersburg", "Nevsky", "5", "main entrance");
    }

    @Test
    void toLongDto_shouldReturnCorrectDto() {
        LocationResponseLong expected =
                new LocationResponseLong("Moscow", "Tverskaya", "10", "near metro");

        assertEquals(expected, mapper.toLongDto(entity));
    }

    @Test
    void toEntity_shouldMapCreateRequestCorrectly() {
        Location result = mapper.toEntity(createRequest);

        assertEquals("Saint Petersburg", result.getCity());
        assertEquals("Nevsky", result.getStreet());
        assertEquals("5", result.getHouse());
        assertEquals("main entrance", result.getAdditionalInfo());
    }
}
