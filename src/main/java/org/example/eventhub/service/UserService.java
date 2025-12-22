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
        checkIsUserAlreadyExistsByUsername(dto.username());

        checkIsUserAlreadyExistsByEmail(dto.email());

        return mapper.toLongDto(repository.save(mapper.toEntity(dto)));
    }

    public Page<UserResponseShort> getAllUsers(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toShortDto);
    }

     public UserResponseLong getUserById(Long id) {
        User user = getUserByIdAsEntity(id);
        return mapper.toLongDto(user);
     }

    public UserResponseLong getUserByUsername(String username) {
        checkIsUserAlreadyExistsByUsername(username);

        User user = repository.findByUsername(username).get();
        return mapper.toLongDto(user);
    }

    public UserResponseLong updateUser(Long id, UserUpdateRequest dto) {
        User user = getUserByIdAsEntity(id);

        if (dto.username() != null) {
            checkIsUserAlreadyExistsByUsername(dto.username());

            user.setUsername(dto.username());
        }

        if (dto.email() != null) {
            checkIsUserAlreadyExistsByEmail(dto.email());

            user.setEmail(dto.email());
        }

        if (dto.password() != null) {
            user.setPassword(dto.password());
        }

        return mapper.toLongDto(repository.save(user));
    }

    public void deleteUser(Long id) {
        getUserByIdAsEntity(id);

        repository.deleteById(id);
    }

    private void checkIsUserAlreadyExistsByUsername(String username) {
        if (repository.findByUsername(username).isPresent())
            throw new UserAlreadyExistsException("Пользователь с username " + username + " уже существует");
    }

    private void checkIsUserAlreadyExistsByEmail(String email) {
        if (repository.findByEmail(email).isPresent())
            throw new UserAlreadyExistsException("Пользователь с email " + email + " уже существует");
    }

    User getUserByIdAsEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));
    }
}
