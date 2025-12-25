package org.example.eventhub.dto.location;

import jakarta.persistence.Column;

public record LocationUpdateRequest(
        @Column(length = 100) String city,
        @Column(length = 200) String street,
        @Column(length = 20) String house,
        String additionalInfo) {}
