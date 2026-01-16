package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.security.LoginRequest
import org.example.eventhub.dto.security.LoginResponse
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val service: AuthService
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: LoginRequest): LoginResponse =
        service.login(dto)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    fun register(@Valid @RequestBody dto: RegisterRequest): LoginResponse =
        service.register(dto)
}