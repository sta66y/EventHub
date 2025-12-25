package org.example.eventhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.eventhub.dto.event.*;
import org.example.eventhub.dto.location.LocationCreateRequest;
import org.example.eventhub.dto.location.LocationResponseLong;
import org.example.eventhub.dto.location.LocationUpdateRequest;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Location;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private EventMapper eventMapper;

    private User organizer;
    private Event event;
    private Location location;

    private EventCreateRequest createRequest;
    private EventUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        organizer = User.builder().id(10L).username("organizer").build();

        location = Location.builder()
                .city("Moscow")
                .street("Tverskaya")
                .house("10")
                .additionalInfo("near metro")
                .build();

        event = Event.builder()
                .id(1L)
                .title("Concert")
                .description("Great show")
                .dateTime(LocalDateTime.of(2026, 1, 15, 20, 0))
                .location(location)
                .capacity(500)
                .price(BigDecimal.valueOf(3000))
                .eventStatus(EventStatus.PUBLISHED)
                .organizer(organizer)
                .build();

        createRequest = new EventCreateRequest(
                "New Event",
                "Amazing event",
                LocalDateTime.of(2026, 2, 20, 19, 0),
                new LocationCreateRequest("SPb", "Nevsky", "5", null),
                300,
                BigDecimal.valueOf(2000),
                EventStatus.DRAFT);

        updateRequest = new EventUpdateRequest(
                "Updated Title",
                "New description",
                LocalDateTime.of(2026, 3, 1, 18, 0),
                new LocationUpdateRequest("Kazan", null, null, "center"),
                400,
                BigDecimal.valueOf(2500),
                EventStatus.PUBLISHED);
    }

    @Test
    @DisplayName("toShortDto: возвращает правильный Short DTO")
    void toShortDto_shouldReturnCorrectDto() {
        EventResponseShort expected = new EventResponseShort(1L, "Concert", LocalDateTime.of(2026, 1, 15, 20, 0));

        assertEquals(expected, eventMapper.toShortDto(event));
    }

    @Test
    @DisplayName("toLongDto: возвращает правильный Long DTO с маппингом вложенных объектов")
    void toLongDto_shouldReturnCorrectDto() {
        UserResponseShort organizerShort = new UserResponseShort("organizer");
        LocationResponseLong locationLong = new LocationResponseLong("Moscow", "Tverskaya", "10", "near metro");

        when(userMapper.toShortDto(organizer)).thenReturn(organizerShort);
        when(locationMapper.toLongDto(location)).thenReturn(locationLong);

        EventResponseLong expected = new EventResponseLong(
                1L,
                "Concert",
                "Great show",
                LocalDateTime.of(2026, 1, 15, 20, 0),
                locationLong,
                500,
                BigDecimal.valueOf(3000),
                EventStatus.PUBLISHED,
                organizerShort);

        assertEquals(expected, eventMapper.toLongDto(event));

        verify(userMapper).toShortDto(organizer);
        verify(locationMapper).toLongDto(location);
    }

    @Test
    @DisplayName("toEntity: маппит CreateRequest в новую сущность Event с вложенной Location")
    void toEntity_shouldMapCreateRequestCorrectly() {
        LocationCreateRequest locDto = createRequest.location();
        Location newLocation = Location.builder()
                .city("SPb")
                .street("Nevsky")
                .house("5")
                .additionalInfo(null)
                .build();

        when(locationMapper.toEntity(locDto)).thenReturn(newLocation);

        Event result = eventMapper.toEntity(createRequest, organizer);

        assertEquals("New Event", result.getTitle());
        assertEquals("Amazing event", result.getDescription());
        assertEquals(LocalDateTime.of(2026, 2, 20, 19, 0), result.getDateTime());
        assertEquals(newLocation, result.getLocation());
        assertEquals(300, result.getCapacity());
        assertEquals(BigDecimal.valueOf(2000), result.getPrice());
        assertEquals(EventStatus.DRAFT, result.getEventStatus());
        assertEquals(organizer, result.getOrganizer());

        verify(locationMapper).toEntity(locDto);
    }

    @Test
    @DisplayName("updateEntity: обновляет только переданные поля, включая частичное обновление Location")
    void updateEntity_shouldUpdateOnlyProvidedFields() {
        Location existingLocation = event.getLocation();

        eventMapper.updateEntity(updateRequest, event);

        assertEquals("Updated Title", event.getTitle());
        assertEquals("New description", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 3, 1, 18, 0), event.getDateTime());
        assertEquals(400, event.getCapacity());
        assertEquals(BigDecimal.valueOf(2500), event.getPrice());
        assertEquals(EventStatus.PUBLISHED, event.getEventStatus());

        assertEquals("Kazan", existingLocation.getCity());
        assertEquals("Tverskaya", existingLocation.getStreet());
        assertEquals("10", existingLocation.getHouse());
        assertEquals("center", existingLocation.getAdditionalInfo());

        verifyNoInteractions(locationMapper);
    }

    @Test
    @DisplayName("updateEntity: не меняет поля, если они null в UpdateRequest")
    void updateEntity_shouldNotChangeFieldsWhenNull() {
        EventUpdateRequest partial = new EventUpdateRequest(null, null, null, null, null, null, null);

        String originalTitle = event.getTitle();
        LocalDateTime originalDate = event.getDateTime();

        eventMapper.updateEntity(partial, event);

        assertEquals(originalTitle, event.getTitle());
        assertEquals(originalDate, event.getDateTime());
    }
}
