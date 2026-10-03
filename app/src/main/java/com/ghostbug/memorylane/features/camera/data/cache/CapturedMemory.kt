package com.ghostbug.memorylane.features.camera.data.cache

import android.net.Uri
import kotlinx.datetime.LocalDateTime
import kotlin.time.Instant

data class CapturedMemory(
    val uri : Uri,
    val latitude: Double?,
    val longitude: Double?,
    val timeStamp : Instant
)
