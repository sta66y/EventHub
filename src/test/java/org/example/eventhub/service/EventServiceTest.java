package org.example.eventhub.service;

import org.example.eventhub.dto.event.*;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.EventStatus;
import org.example.eventhub.exception.EventNotFoundException;
import org.example.eventhub.mapper.EventMapper;
import org.example.eventhub.repository.EventRepository;
import org.example.eventhub.specification.EventSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;
    private static final Long ORGANIZER_ID = 10L;

    private final EventRepository repository = mock(EventRepository.class);

    private final UserService userService = mock(UserService.class);

    private final EventMapper mapper = mock(EventMapper.class);

    private final EventSpecification realSpecification = new EventSpecification();

    private final EventService eventService = new EventService(
            repository, userService, mapper, realSpecification
    );

    private User organizer;
    private Event event;
    private EventCreateRequest createRequest;
    private EventUpdateRequest updateRequest;
    private EventResponseLong responseLong;
    private EventResponseShort responseShort;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(ORGANIZER_ID);

        event = new Event();
        event.setId(EXISTING_ID);
        event.setTitle("Test Event");
        event.setOrganizer(organizer);
        event.setEventStatus(EventStatus.DRAFT);

        createRequest = new EventCreateRequest(
                "New Event",
                "Description",
                LocalDateTime.now().plusDays(10),
                null,
                50,
                BigDecimal.TEN,
                null
        );

        updateRequest = new EventUpdateRequest(
                "Updated Title",
                "Updated Desc",
                LocalDateTime.now().plusDays(20),
                null,
                100,
                BigDecimal.valueOf(20),
                EventStatus.PUBLISHED
        );

        responseLong = new EventResponseLong(
                EXISTING_ID,
                "Updated Title",
                "Updated Desc",
                LocalDateTime.now().plusDays(20),
                null,
                100,
                BigDecimal.valueOf(20),
                EventStatus.PUBLISHED,
                null
        );

        responseShort = new EventResponseShort(
                EXISTING_ID,
                "Test Event",
                LocalDateTime.now().plusDays(10)
        );

        pageable = PageRequest.of(0, 10, Sort.by("startDate").descending());
    }

    // ====================== createEvent ======================

    @Test
    @DisplayName("createEvent: успешно создаёт событие и возвращает Long DTO")
    void createEvent_success() {
        when(userService.getUserByIdAsEntity(ORGANIZER_ID)).thenReturn(organizer);
        when(mapper.toEntity(createRequest, organizer)).thenReturn(event);
        when(repository.save(event)).thenReturn(event);
        when(mapper.toLongDto(event)).thenReturn(responseLong);

        EventResponseLong result = eventService.createEvent(createRequest, ORGANIZER_ID);

        assertEquals(responseLong, result);
        verify(repository).save(event);
    }

    // ====================== getEventById ======================

    @Test
    @DisplayName("getEventById: возвращает Long DTO при существующем событии")
    void getEventById_success() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(event));
        when(mapper.toLongDto(event)).thenReturn(responseLong);

        EventResponseLong result = eventService.getEventById(EXISTING_ID);

        assertEquals(responseLong, result);
    }

    @Test
    @DisplayName("getEventById: бросает исключение, если событие не найдено")
    void getEventById_notFound() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        EventNotFoundException ex = assertThrows(EventNotFoundException.class,
                () -> eventService.getEventById(NON_EXISTING_ID));

        assertEquals("Event с id " + NON_EXISTING_ID + " не найден", ex.getMessage());
    }

    // ====================== getEventByIdAsEntity ======================

    @Test
    @DisplayName("getEventByIdAsEntity: возвращает сущность, если существует")
    void getEventByIdAsEntity_found() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(event));

        Event result = eventService.getEventByIdAsEntity(EXISTING_ID);

        assertEquals(event, result);
    }

    @Test
    @DisplayName("getEventByIdAsEntity: бросает исключение, если не найдено")
    void getEventByIdAsEntity_notFound() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        EventNotFoundException ex = assertThrows(EventNotFoundException.class,
                () -> eventService.getEventByIdAsEntity(NON_EXISTING_ID));

        assertEquals("Event с id " + NON_EXISTING_ID + " не найден", ex.getMessage());
    }

    // ====================== updateEvent ======================

    @Test
    @DisplayName("updateEvent: успешно обновляет и возвращает Long DTO")
    void updateEvent_success() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(event));
        when(repository.save(event)).thenReturn(event);
        when(mapper.toLongDto(event)).thenReturn(responseLong);

        EventResponseLong result = eventService.updateEvent(EXISTING_ID, updateRequest);

        verify(mapper).updateEntity(updateRequest, event);
        verify(repository).save(event);
        assertEquals(responseLong, result);
    }

    @Test
    @DisplayName("updateEvent: бросает исключение, если событие не найдено")
    void updateEvent_notFound() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> eventService.updateEvent(NON_EXISTING_ID, updateRequest));
    }

    // ====================== deleteEvent ======================

    @Test
    @DisplayName("deleteEvent: успешно удаляет существующее событие")
    void deleteEvent_success() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(event));

        eventService.deleteEvent(EXISTING_ID);

        verify(repository).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("deleteEvent: бросает исключение, если событие не найдено")
    void deleteEvent_notFound() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> eventService.deleteEvent(NON_EXISTING_ID));
    }

    // ====================== getAllEvents ======================

    @Test
    @DisplayName("getAllEvents: возвращает страницу Short DTO с применённым фильтром")
    void getAllEvents_withFilter_appliesSpecification() {
        EventFilter filter = new EventFilter(
                "concert",
                null,
                null, null,
                null, null,
                null,
                null, null,
                true
        );

        List<Event> events = List.of(event);
        Page<Event> page = new PageImpl<>(events, pageable, events.size());

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toShortDto(event)).thenReturn(responseShort);

        Page<EventResponseShort> result = eventService.getAllEvents(pageable, filter);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseShort, result.getContent().get(0));

        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAllEvents: работает с пустым фильтром и пустой страницей")
    void getAllEvents_emptyResult() {
        Page<Event> emptyPage = Page.empty(pageable);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<EventResponseShort> result = eventService.getAllEvents(pageable, null);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }
}