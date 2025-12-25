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

        createRequest = new LocationCreateRequest("Saint Petersburg", "Nevsky Prospect", "5", "main entrance");
    }

    @Test
    @DisplayName("toLongDto: возвращает правильный Long DTO")
    void toLongDto_shouldReturnCorrectDto() {
        LocationResponseLong expected = new LocationResponseLong("Moscow", "Tverskaya", "10", "near metro");

        assertEquals(expected, mapper.toLongDto(entity));
    }

    @Test
    @DisplayName("toEntity: маппит CreateRequest в сущность Location")
    void toEntity_shouldMapCreateRequestCorrectly() {
        Location expected = new Location("Saint Petersburg", "Nevsky Prospect", "5", "main entrance");

        Location result = mapper.toEntity(createRequest);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("toLongDto: корректно работает с null в additionalInfo")
    void toLongDto_handlesNullAdditionalInfo() {
        Location nullInfoEntity = new Location("Kazan", "Bauman", "1", null);

        LocationResponseLong expected = new LocationResponseLong("Kazan", "Bauman", "1", null);

        assertEquals(expected, mapper.toLongDto(nullInfoEntity));
    }

    @Test
    @DisplayName("toEntity: корректно работает с null в additionalInfo")
    void toEntity_handlesNullAdditionalInfo() {
        LocationCreateRequest nullInfoRequest = new LocationCreateRequest("Novosibirsk", "Krasny Prospect", "20", null);

        Location expected = new Location("Novosibirsk", "Krasny Prospect", "20", null);

        assertEquals(expected, mapper.toEntity(nullInfoRequest));
    }
}
