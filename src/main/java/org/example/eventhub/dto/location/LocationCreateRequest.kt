package org.example.eventhub.dto.location

import jakarta.validation.constraints.NotBlank

data class LocationCreateRequest(
    val city: @NotBlank String,
    val street: @NotBlank String,
    val house: @NotBlank String,
    val additionalInfo: String?
)
