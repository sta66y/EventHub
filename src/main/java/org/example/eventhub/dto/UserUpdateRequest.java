package org.example.eventhub.dto;

public record UserUpdateRequest(
        String username,
        String email,
        String password
){
}
