package org.example.eventhub.dto.security

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    val email: @NotBlank(message = "Email обязателен") @Email(message = "Некорректный формат email") String,
    val password: @NotBlank(message = "Password обязателен") @Size(
        min = 6,
        message = "Длина пароля не может быть меньше 6"
    ) String
)