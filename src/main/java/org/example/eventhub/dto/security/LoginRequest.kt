package org.example.eventhub.dto.security

data class LoginRequest(
    val username: String,
    val password: String
)