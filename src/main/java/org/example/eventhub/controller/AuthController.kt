package org.example.eventhub.controller

import org.example.eventhub.dto.security.LoginRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/login")
    fun login(@RequestBody dto: LoginRequest) {
        val authToken = UsernamePasswordAuthenticationToken(
            dto.username,
            dto.password
        )

        authenticationManager.authenticate(authToken)
    }
}