package org.example.eventhub.dto.location

data class LocationUpdateRequest(
    val city: String?,
    val street: String?,
    val house: String?,
    val additionalInfo: String?
)
