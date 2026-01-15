package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.dto.user.*
import org.example.eventhub.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
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
    @PreAuthorize("hasAuthority('USER_READ')")
    fun getAllUsers(pageable: Pageable, filter: UserFilter): Page<UserResponseShort> =
        service.getAllUsers(pageable, filter)

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('USER_READ')")
    fun searchUser(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) username: String?
    ): UserResponseLong {
        require(userId != null || username != null) {
            "Нужно передать userId или username"
        }
        require(!(userId != null && username != null)) {
            "Передавайте только один параметр"
        }

        return when {
            userId != null -> service.getUserById(userId)
            else -> service.getUserByUsername(username!!)
        }
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    fun updateUser(
        @AuthenticationPrincipal user: UserDetails,
        @Valid @RequestBody dto: UserUpdateRequest
    ): UserResponseLong =
        service.updateUser(user, dto)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USER_DELETE')")
    fun deleteUser(@AuthenticationPrincipal user: UserDetails) =
        service.deleteUser(user)
}