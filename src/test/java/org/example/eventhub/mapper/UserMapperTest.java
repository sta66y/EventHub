package org.example.eventhub.mapper;

import org.example.eventhub.dto.user.UserCreateRequest;
import org.example.eventhub.dto.user.UserResponseLong;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.example.eventhub.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    private User user;
    private UserCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        List<Event> organizedEvents = List.of(new Event(), new Event());
        user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("secret")
                .role(Role.USER)
                .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                .organizedEvents(organizedEvents)
                .build();

        createRequest = new UserCreateRequest(
                "new_user",
                "new@example.com",
                "pass123"
        );
    }

    @Test
    @DisplayName("toShortDto: должен вернуть правильное short DTO")
    void toShortDto_shouldReturnRightDto() {
        UserResponseShort dto = new UserResponseShort("john_doe");

        assertEquals(dto, mapper.toShortDto(user));
    }

    @Test
    @DisplayName("toLongDto: должен вернуть правильное long DTO")
    void toLongDto_shouldReturnRightDto() {
        UserResponseLong dto = new UserResponseLong(
                1L, "john_doe", "john@example.com",
                Role.USER, LocalDateTime.of(2025, 1, 1, 12, 0),
                2
        );

        assertEquals(dto, mapper.toLongDto(user));
    }

    @Test
    @DisplayName("toEntity: должен вернуть Entity")
    void toEntity_shouldReturnRightEntity() {
        User mappedUser = mapper.toEntity(createRequest);

        assertEquals("new_user", mappedUser.getUsername());
        assertEquals("new@example.com", mappedUser.getEmail());
        assertEquals("pass123", mappedUser.getPassword());
    }
}
