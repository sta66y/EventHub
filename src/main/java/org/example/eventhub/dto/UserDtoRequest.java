package org.example.eventhub.dto;

public record UserDtoRequest(
    String username,
    String email,
    String password
){
}
