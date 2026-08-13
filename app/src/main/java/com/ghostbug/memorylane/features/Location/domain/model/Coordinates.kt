package com.ghostbug.memorylane.features.Location.domain.model

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long?,

    // Optional/Advanced fields (Null if the hardware couldn't calculate them)
    val horizontalAccuracy: Double? = null,
    val bearing: Double? = null,
    val speed: Double? = null,
    val altitude: Double? = null
)
