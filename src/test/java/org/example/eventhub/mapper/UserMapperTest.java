package org.example.eventhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.eventhub.dto.user.UserResponseLong;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.Role;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper(password -> password);

    @Test
    void toShortDto() {
        User user = new User(1L, "john", "mail", "pass", Role.USER, null, new ArrayList<>());
        UserResponseShort dto = mapper.toShortDto(user);

        assertEquals("john", dto.username());
    }

    @Test
    void toLongDto() {
        User user = new User(
                1L,
                "john",
                "mail",
                "pass",
                Role.USER,
                LocalDateTime.of(2025, 1, 1, 12, 0),
                List.of(new Event(), new Event())
        );

        UserResponseLong dto = mapper.toLongDto(user);

        assertEquals(2, dto.countOrganizedEvents());
    }
}
