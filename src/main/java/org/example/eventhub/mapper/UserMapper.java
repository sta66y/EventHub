package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.UserDtoRequest;
import org.example.eventhub.dto.UserResponseLong;
import org.example.eventhub.dto.UserResponseShort;
import org.example.eventhub.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final EventMapper eventMapper;

    public UserResponseShort toShortDto(User user) {
        return new UserResponseShort(user.getUsername());
    }

    public UserResponseLong toLongDto(User user) {
        return new UserResponseLong(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getOrganizedEvents().stream().map(eventMapper::toShortDto).toList()
        );
    }

    public User toEntity(UserDtoRequest dto) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .build();
    }
}
