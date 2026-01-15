package org.example.eventhub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.eventhub.dto.security.RegisterRequest;
import org.example.eventhub.dto.user.*;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.Role;
import org.example.eventhub.exception.UserAlreadyExistsException;
import org.example.eventhub.exception.UserNotFoundException;
import org.example.eventhub.mapper.UserMapper;
import org.example.eventhub.repository.UserRepository;
import org.example.eventhub.specification.UserSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

class UserServiceTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;
    private static final String EXISTING_USERNAME = "john_doe";
    private static final String EXISTING_EMAIL = "john@example.com";
    private static final String NEW_USERNAME = "jane_doe";
    private static final String NEW_EMAIL = "jane@example.com";

    private final UserRepository repository = mock(UserRepository.class);
    private final UserMapper mapper = mock(UserMapper.class);
    private final UserSpecification realSpecification = new UserSpecification();

    private final UserService userService = new UserService(repository, mapper, realSpecification);

    private User user;
    private RegisterRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserResponseLong responseLong;
    private UserResponseShort responseShort;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(EXISTING_ID);
        user.setUsername(EXISTING_USERNAME);
        user.setEmail(EXISTING_EMAIL);
        user.setPassword("oldPassword");

        createRequest = new RegisterRequest("new_user", "new@example.com", "password123");

        updateRequest = new UserUpdateRequest(NEW_USERNAME, NEW_EMAIL, "newPassword");

        responseLong = new UserResponseLong(
                EXISTING_ID, EXISTING_USERNAME, EXISTING_EMAIL, Role.USER, LocalDateTime.now(), 10);

        responseShort = new UserResponseShort(EXISTING_USERNAME);

        pageable = PageRequest.of(0, 10);
    }

    // ====================== createUser ======================

    @Test
    @DisplayName("createUser: успешно создаёт нового пользователя")
    void createUser_success() {
        when(repository.findByUsername(createRequest.username)).thenReturn(Optional.empty());
        when(repository.findByEmail(createRequest.email)).thenReturn(Optional.empty());
        when(mapper.toEntity(createRequest)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toLongDto(user)).thenReturn(responseLong);

        UserResponseLong result = userService.createUser(createRequest);

        assertEquals(responseLong, result);
        verify(repository).save(user);
    }

    @Test
    @DisplayName("createUser: бросает исключение, если username уже существует")
    void createUser_usernameAlreadyExists() {
        when(repository.findByUsername(createRequest.username)).thenReturn(Optional.of(user));

        UserAlreadyExistsException ex =
                assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createRequest));

        assertEquals("Пользователь с username " + createRequest.username + " уже существует", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("createUser: бросает исключение, если email уже существует")
    void createUser_emailAlreadyExists() {
        when(repository.findByUsername(createRequest.username)).thenReturn(Optional.empty());
        when(repository.findByEmail(createRequest.email)).thenReturn(Optional.of(user));

        UserAlreadyExistsException ex =
                assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createRequest));

        assertEquals("Пользователь с email " + createRequest.email + " уже существует", ex.getMessage());
        verify(repository, never()).save(any());
    }

    // ====================== getAllUsers ======================

    @Test
    @DisplayName("getAllUsers: возвращает страницу Short DTO с фильтром")
    void getAllUsers_returnsPage() {
        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);
        UserFilter filter = new UserFilter("john", null, null, null, null, null);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toShortDto(user)).thenReturn(responseShort);

        Page<UserResponseShort> result = userService.getAllUsers(pageable, filter);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseShort, result.getContent().get(0));
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAllUsers: возвращает пустую страницу")
    void getAllUsers_emptyPage() {
        Page<User> emptyPage = Page.empty(pageable);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<UserResponseShort> result = userService.getAllUsers(pageable, null);

        assertTrue(result.isEmpty());
    }

    // ====================== getUserById ======================

    @Test
    @DisplayName("getUserById: возвращает Long DTO при существующем пользователе")
    void getUserById_success() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(user));
        when(mapper.toLongDto(user)).thenReturn(responseLong);

        UserResponseLong result = userService.getUserById(EXISTING_ID);

        assertEquals(responseLong, result);
    }

    @Test
    @DisplayName("getUserById: бросает исключение, если пользователь не найден")
    void getUserById_notFound() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        UserNotFoundException ex =
                assertThrows(UserNotFoundException.class, () -> userService.getUserById(NON_EXISTING_ID));

        assertEquals("Пользователь с id " + NON_EXISTING_ID + " не найден", ex.getMessage());
    }

    // ====================== getUserByUsername ======================

    @Test
    @DisplayName("getUserByUsername: возвращает Long DTO по username")
    void getUserByUsername_success() {
        when(repository.findByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(user));
        when(mapper.toLongDto(user)).thenReturn(responseLong);

        UserResponseLong result = userService.getUserByUsername(EXISTING_USERNAME);

        assertEquals(responseLong, result);
    }

    @Test
    @DisplayName("getUserByUsername: бросает исключение, если пользователь не найден")
    void getUserByUsername_notFound() {
        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserNotFoundException ex =
                assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("unknown"));

        assertEquals("Пользователь с username unknown не найден", ex.getMessage());
    }

    // ====================== updateUser ======================

    @Test
    @DisplayName("updateUser: успешно обновляет все поля")
    void updateUser_success_allFields() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(user));
        when(repository.findByUsername(NEW_USERNAME)).thenReturn(Optional.empty());
        when(repository.findByEmail(NEW_EMAIL)).thenReturn(Optional.empty());
        when(repository.save(user)).thenReturn(user);
        when(mapper.toLongDto(user)).thenReturn(responseLong);

        UserResponseLong result = userService.updateUser(EXISTING_ID, updateRequest);

        assertEquals(NEW_USERNAME, user.getUsername());
        assertEquals(NEW_EMAIL, user.getEmail());
        assertEquals("newPassword", user.getPassword());
        assertEquals(responseLong, result);
        verify(repository).save(user);
    }

    @Test
    @DisplayName("updateUser: обновляет только email, если username не меняется")
    void updateUser_onlyEmailChanged() {
        UserUpdateRequest partial = new UserUpdateRequest(null, NEW_EMAIL, null);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(user));
        when(repository.findByEmail(NEW_EMAIL)).thenReturn(Optional.empty());
        when(repository.save(user)).thenReturn(user);
        when(mapper.toLongDto(user)).thenReturn(responseLong);

        userService.updateUser(EXISTING_ID, partial);

        assertEquals(EXISTING_USERNAME, user.getUsername());
        assertEquals(NEW_EMAIL, user.getEmail());
    }

    @Test
    @DisplayName("updateUser: бросает исключение при попытке взять занятый username")
    void updateUser_usernameAlreadyTaken() {
        UserUpdateRequest badRequest = new UserUpdateRequest("taken_user", null, null);
        User anotherUser = new User();
        anotherUser.setUsername("taken_user");

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(user));
        when(repository.findByUsername("taken_user")).thenReturn(Optional.of(anotherUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(EXISTING_ID, badRequest));
    }

    // ====================== deleteUser ======================

    @Test
    @DisplayName("deleteUser: успешно удаляет существующего пользователя")
    void deleteUser_success() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(user));

        userService.deleteUser(EXISTING_ID);

        verify(repository).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("deleteUser: бросает исключение, если пользователь не найден")
    void deleteUser_notFound() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(NON_EXISTING_ID));
    }
}
