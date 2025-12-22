package org.example.eventhub.service;

import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.user.UserCreateRequest;
import org.example.eventhub.dto.user.UserResponseLong;
import org.example.eventhub.dto.user.UserResponseShort;
import org.example.eventhub.dto.user.UserUpdateRequest;
import org.example.eventhub.exception.UserAlreadyExistsException;
import org.example.eventhub.exception.UserNotFoundException;
import org.example.eventhub.mapper.UserMapper;
import org.example.eventhub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.example.eventhub.entity.User;

import java.awt.print.Pageable;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserResponseLong createUser(UserCreateRequest dto) {
        if (repository.findByUsername(dto.username()).isPresent())
            throw new UserAlreadyExistsException("Пользователь с username " + dto.username() + " уже существует");

        if (repository.findByEmail(dto.email()).isPresent())
            throw new UserAlreadyExistsException("Пользователь с email " + dto.email() + " уже существует");

        return mapper.toLongDto(repository.save(mapper.toEntity(dto)));
    }

    public Page<UserResponseShort> getAllUsers(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toShortDto);
    }

     public UserResponseLong getUserById(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));
        return mapper.toLongDto(user);
     }

    User getUserByIdAsEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));
    }

    public UserResponseLong getUserByUsername(String username) {
        User user = repository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Пользователь с username " + username + " не найден"));
        return mapper.toLongDto(user);
    }

    public UserResponseLong updateUser(Long id, UserUpdateRequest dto) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        if (dto.username() != null) {
            if (repository.findByUsername(dto.username()).isPresent())
                throw new UserAlreadyExistsException("Пользователь с username " + dto.username() + " уже существует");

            user.setUsername(dto.username());
        }

        if (dto.email() != null) {
            if (repository.findByEmail(dto.email()).isPresent())
                throw new UserAlreadyExistsException("Пользователь с email " + dto.email() + " уже существует");

            user.setEmail(dto.email());
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
