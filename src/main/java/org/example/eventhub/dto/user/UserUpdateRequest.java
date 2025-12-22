package org.example.eventhub.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 3, max = 50, message = "Длина username должна лежать в диапазоне от 3 до 50")
        String username,
        @Email(message = "Некорректный формат email")
        String email,
        @Size(min = 6, message = "Длина пароля не может быть меньше 6")
        String password
){
}
