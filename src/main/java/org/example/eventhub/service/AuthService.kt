package org.example.eventhub.service

import org.example.eventhub.dto.security.LoginRequest
import org.example.eventhub.dto.security.LoginResponse
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) {
    fun login(dto: LoginRequest): LoginResponse =
        generateToken(dto)

    fun register(dto: RegisterRequest): LoginResponse {
        userService.createUser(dto)
        return login(LoginRequest(dto.email, dto.password))
    }


    private fun generateToken(dto: LoginRequest): LoginResponse {
        val authToken = UsernamePasswordAuthenticationToken(
            dto.email,
            dto.password
        )

        val authentication = authenticationManager.authenticate(authToken)

        val token = jwtTokenProvider.generateToken(
            authentication.name,
            authentication.authorities.map { it.authority!! }
        )

        return LoginResponse(token)
    }
}