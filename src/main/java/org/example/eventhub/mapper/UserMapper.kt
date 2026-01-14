package org.example.eventhub.mapper;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.user.UserCreateRequest;
import org.example.eventhub.dto.user.UserResponseLong;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponseShort toShortDto(User entity) {
        return new UserResponseShort(entity.getUsername());
    }

    public UserResponseLong toLongDto(User entity) {
        return new UserResponseLong(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getOrganizedEvents().size());
    }

    public User toEntity(UserCreateRequest dto) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .build();
    }
}
