package com.ghostbug.memorylane.features.camera.data.dto

import com.ghostbug.memorylane.features.camera.domain.model.Memories
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data  class MemoriesDto (
    @SerialName("id")
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("image_path")
    val imagePath: String,
    @SerialName("caption")
    val caption: String,
    @SerialName("img_latitude")
    val imgLatitude: Double,
    @SerialName("img_longitude")
    val imgLongitude: Double,
    @SerialName("captured_at")
    val capturedAt: Instant,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("place_name")
    val place: String,
)

//fun Memories.toDto()=MemoriesDto(
//    id = id,
//    userId = userId,
//    imagePath = imagePath,
//    caption = caption,
//    imgLatitude = imgLatitude,
//    imgLongitude = imgLongitude,
//    capturedAt = capturedAt,
//    createdAt = createdAt,
//    place = place,
//)
//
//fun MemoriesDto.toMemory()=Memories(
//    id = id,
//    userId = userId,
//    imagePath = imagePath,
//    caption = caption,
//    imgLatitude = imgLatitude,
//    imgLongitude = imgLongitude,
//    capturedAt = capturedAt,
//    createdAt = createdAt,
//    place = place,
//)

@Serializable
data class MemoryInsertDto(
    @SerialName("user_id")
    val userId: String,
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