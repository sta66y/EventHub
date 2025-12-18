package org.example.eventhub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.UserCreateRequest;
import org.example.eventhub.dto.UserResponseLong;
import org.example.eventhub.dto.UserResponseShort;
import org.example.eventhub.dto.UserUpdateRequest;
import org.example.eventhub.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseShort> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseLong createUser(@Valid @RequestBody UserCreateRequest dto) {
        return service.createUser(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseLong getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseLong getUserByUsername(@RequestParam String username) {
        return service.getUserByUsername(username);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseLong updateUser(@PathVariable Long id, @Valid UserUpdateRequest dto) {
        return service.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
