package org.example.eventhub.dto.location;

public record LocationResponseLong(
        String city,
        String street,
        String house,

        String additionalInfo
) {
}
