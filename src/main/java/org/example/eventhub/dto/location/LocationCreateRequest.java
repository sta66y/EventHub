package org.example.eventhub.dto.location;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record LocationCreateRequest(
        @Column(length = 100) @NotBlank String city,
        @Column(length = 200) @NotBlank String street,
        @Column(length = 20) @NotBlank String house,
        String additionalInfo) {}
