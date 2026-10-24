package com.ghostbug.memorylane.features.capture.domain.cache

import android.net.Uri
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

data class CapturedMemory (
    val uri : Uri, //image path in the app cache
    val imgLatitude: Double?,
    val imgLongitude: Double?,
    val caption: String,
    val placeName : String?=null,
    val capturedAt : Instant
)
// Extension to convert local CapturedMemory -> Memories domain model
@OptIn(ExperimentalUuidApi::class)
fun CapturedMemory.toDomain(): Memories {
    return Memories(
        id = kotlin.uuid.Uuid.random().toString(),
        userId = "",
        imagePath = "",
        imageUrl = uri.toString(), // Uses local file URI for instant rendering!
        caption = caption,
        imgLatitude = imgLatitude ?: 0.0,
        imgLongitude = imgLongitude ?: 0.0,
        capturedAt = capturedAt,
        createdAt = capturedAt,
        place = placeName ?:""
    )
}