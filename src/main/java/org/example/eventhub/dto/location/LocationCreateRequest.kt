package org.example.eventhub.dto.location

import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank

data class LocationCreateRequest(
    @Column(length = 100) val city: @NotBlank String,
    @Column(length = 200) val street: @NotBlank String,
    @Column(length = 20) val house: @NotBlank String,
    val additionalInfo: String?
)
