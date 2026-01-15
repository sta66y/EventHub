package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.dto.user.*
import org.example.eventhub.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val service: UserService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllUsers(pageable: Pageable, filter: UserFilter): Page<UserResponseShort> =
        service.getAllUsers(pageable, filter)

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    fun getUserById(@PathVariable userId: Long): UserResponseLong =
        service.getUserById(userId)

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    fun getUserByUsername(@RequestParam username: String): UserResponseLong =
        service.getUserByUsername(username)

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateUser(
        @AuthenticationPrincipal user: UserDetails,
        @Valid @RequestBody dto: UserUpdateRequest
    ): UserResponseLong =
        service.updateUser(user, dto)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@AuthenticationPrincipal user: UserDetails) =
        service.deleteUser(user)
}