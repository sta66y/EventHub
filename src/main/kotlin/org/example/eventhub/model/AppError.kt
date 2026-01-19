package org.example.eventhub.model

data class AppError(
    val message: String,
    val status: Int,
    val details: Map<String, String>? = null
)
