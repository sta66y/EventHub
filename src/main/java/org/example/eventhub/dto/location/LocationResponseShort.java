package org.example.eventhub.dto.location;

public record LocationResponseShort(
        String city,
        String street,
        String house
) {
}
