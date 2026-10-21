package com.ghostbug.memorylane.features.capture.domain.cache

import android.net.Uri
import kotlin.time.Instant

data class CapturedMemory (
    val uri : Uri, //image path in the app cache
    val imgLatitude: Double?,
    val imgLongitude: Double?,
    val caption: String,
    val capturedAt : Instant
)
