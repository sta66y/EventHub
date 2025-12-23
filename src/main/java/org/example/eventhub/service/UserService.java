package org.example.eventhub.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.user.*;
import org.example.eventhub.exception.UserAlreadyExistsException;
import org.example.eventhub.exception.UserNotFoundException;
import org.example.eventhub.mapper.UserMapper;
import org.example.eventhub.repository.UserRepository;
import org.example.eventhub.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.example.eventhub.entity.User;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserSpecification specification;

    public UserResponseLong createUser(UserCreateRequest dto) {
        checkIsUserAlreadyExistsByUsername(dto.username());

        checkIsUserAlreadyExistsByEmail(dto.email());

        return mapper.toLongDto(repository.save(mapper.toEntity(dto)));
    }

    public Page<UserResponseShort> getAllUsers(Pageable pageable, UserFilter filter) {
        return repository.findAll(specification.withFilter(filter), pageable).map(mapper::toShortDto);
    }

     public UserResponseLong getUserById(Long id) {
        User user = getUserByIdAsEntity(id);
        return mapper.toLongDto(user);
     }

    public UserResponseLong getUserByUsername(String username) {
        User user = getUserByUsernameAsEntity(username);
        return mapper.toLongDto(user);
    }

    public UserResponseLong updateUser(Long id, UserUpdateRequest dto) {
        User user = getUserByIdAsEntity(id);

        if (dto.username() != null && !dto.username().equals(user.getUsername())) {
            checkIsUserAlreadyExistsByUsername(dto.username());
            user.setUsername(dto.username());
        }

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
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

    User getUserByUsernameAsEntity(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с username " + username + " не найден"));
    }
}
