package com.ghostbug.memorylane.features.capture.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data  class MemoriesDto (
    @SerialName("id")
    val id: String,
    @SerialName("image_path")
    val imagePath: String,
    @SerialName("image_url")
    val imageUrl : String,
    @SerialName("caption")
    val caption: String?,
    @SerialName("img_latitude")
    val imgLatitude: Double?,
    @SerialName("img_longitude")
    val imgLongitude: Double?,
    @SerialName("captured_at")
    val capturedAt: Instant,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("place_name")
    val place: String?,
)

@Serializable
data class MemoryInsertDto(
    @SerialName("image_path")
    val imagePath: String,
    @SerialName("caption")
    val caption: String?,
    @SerialName("img_latitude")
    val imgLatitude: Double?,
    @SerialName("img_longitude")
    val imgLongitude: Double?,
    @SerialName("captured_at")
    val capturedAt: Instant,
    @SerialName("place_name")
    val place: String?
)