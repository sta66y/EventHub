package org.example.eventhub.dto.security

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    val username: @NotBlank(message = "Username обязателен") @Size(
        min = 3,
        max = 50,
        message = "Длина username должна лежать в диапазоне от 3 до 50"
    ) String,
    val email: @NotBlank(message = "Email обязателен") @Email(message = "Некорректный формат email") String,
    val password: @NotBlank(message = "Password обязателен") @Size(
        min = 6,
        message = "Длина пароля не может быть меньше 6"
    ) String
)