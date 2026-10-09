package com.ghostbug.memorylane.features.camera.domain.model

import kotlin.time.Instant

data class Memories(
    val id: String,
    val userId: String,
    val imagePath: String,
    val caption: String,
    val imgLatitude: Double,
    val imgLongitude: Double,
    val capturedAt: Instant,
    val createdAt: Instant,
    val place: String,
)