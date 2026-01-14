package org.example.eventhub.dto.location

import jakarta.persistence.Column

data class LocationUpdateRequest(
    @Column(length = 100) val city: String?,
    @Column(length = 200) val street: String?,
    @Column(length = 20) val house: String?,
    val additionalInfo: String?
)
