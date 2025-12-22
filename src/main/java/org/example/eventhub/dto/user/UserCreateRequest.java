package org.example.eventhub.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Username обязателен")
        @Size(min = 3, max = 50, message = "Длина username должна лежать в диапазоне от 3 до 50")
        String username,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Password обязателен")
        @Size(min = 6, message = "Длина пароля не может быть меньше 6")
        String password
){
}
