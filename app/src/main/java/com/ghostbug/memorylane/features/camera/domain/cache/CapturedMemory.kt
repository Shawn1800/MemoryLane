package com.ghostbug.memorylane.features.camera.domain.cache

import android.net.Uri
import kotlinx.datetime.LocalDateTime
import kotlin.time.Instant

data class CapturedMemory(
    val uri : Uri, //image path in the app cache
    val imgLatitude: Double?,
    val imgLongitude: Double?,
    val caption: String,
    val capturedAt : Instant
)
