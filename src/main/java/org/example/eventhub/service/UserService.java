package org.example.eventhub.service;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.UserDtoRequest;
import org.example.eventhub.dto.UserResponseLong;
import org.example.eventhub.dto.UserResponseShort;
import org.example.eventhub.exception.UserAlreadyExists;
import org.example.eventhub.exception.UserNotFoundException;
import org.example.eventhub.mapper.UserMapper;
import org.example.eventhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.example.eventhub.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserResponseLong createUser(UserDtoRequest dto) {
        if (repository.findByUsername(dto.username()).isEmpty())
            throw new UserAlreadyExists("Пользователь с username " + dto.username() + " уже существует");

        if (repository.findByEmail(dto.email()).isEmpty())
            throw new UserAlreadyExists("Пользователь с email " + dto.email() + " уже существует");

        return mapper.toLongDto(repository.save(mapper.toEntity(dto)));
    }

    public List<UserResponseShort> getAllUsers() { // TODO pageable
        return repository.findAll().stream().map(mapper::toShortDto).toList();
    }

     public UserResponseLong getUserById(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));
        return mapper.toLongDto(user);
     }

    public UserResponseLong getUserByUsername(String username) {
        User user = repository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Пользователь с username " + username + " не найден"));
        return mapper.toLongDto(user);
    }

    public UserResponseLong updateUser(Long id, UserDtoRequest dto) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        if (dto.username() != null) {
            if (repository.findByUsername(dto.username()).isPresent())
                throw new UserAlreadyExists("Пользователь с username " + dto.username() + " уже существует");

            user.setUsername(dto.username());
        }

        if (dto.email() != null) {
            if (repository.findByEmail(dto.email()).isPresent())
                throw new UserAlreadyExists("Пользователь с email " + dto.email() + " уже существует");

            user.setUsername(dto.email());
        }

        if (dto.password() != null) {
            user.setPassword(dto.password());
        }

        return mapper.toLongDto(repository.save(user));
    }

    public void deleteUser(Long id) {
        if (repository.findById(id).isEmpty())
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");

        repository.deleteById(id);
    }
}
