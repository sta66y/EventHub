package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.dto.user.*
import org.example.eventhub.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val service: UserService
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllUsers(
        pageable: Pageable,
        filter: UserFilter
    ): Page<UserResponseShort> {
        return service.getAllUsers(pageable, filter)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody dto: RegisterRequest
    ): UserResponseLong {
        return service.createUser(dto)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getUserById(
        @PathVariable id: Long
    ): UserResponseLong {
        return service.getUserById(id)
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    fun getUserByUsername(
        @RequestParam username: String
    ): UserResponseLong {
        return service.getUserByUsername(username)
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody dto: UserUpdateRequest
    ): UserResponseLong {
        return service.updateUser(id, dto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(
        @PathVariable id: Long
    ) {
        service.deleteUser(id)
    }
}