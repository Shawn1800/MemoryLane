package com.ghostbug.memorylane.features.capture.data.repositoryImpl

import android.content.Context
import android.util.Log
import com.ghostbug.memorylane.features.capture.data.dto.MemoriesDto
import com.ghostbug.memorylane.features.capture.data.dto.MemoryInsertDto
import com.ghostbug.memorylane.features.capture.data.dto.toDomain
import com.ghostbug.memorylane.features.capture.data.repositoryImpl.utils.ReverseGeocoder
import com.ghostbug.memorylane.features.capture.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import com.ghostbug.memorylane.features.capture.domain.repository.CameraManagerRepository
import com.mapbox.maps.extension.style.image.image
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.utils.io.CancellationException
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class CameraManagerRepositoryImpl(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val reverseGeocoder: ReverseGeocoder,
    private val context: Context,
    private val storage: Storage
) : CameraManagerRepository {


    @OptIn(ExperimentalUuidApi::class)
    override suspend fun SaveImageToDatabase(capturedMemory: CapturedMemory): Result<String> {
        val user = auth.currentSessionOrNull()?.user
        val userId = user?.id

        val lat = capturedMemory.imgLatitude
        val lng = capturedMemory.imgLongitude

        // place name is nice-to-have: never block the save on it
        val placeName =
            if (lat != null && lng != null) {
                reverseGeocoder.reverseGeocode(lat, lng)
            } else null

        Log.e("CameraManagerRepositoryImpl", "SaveImageToDatabase: $placeName")

        val bytes =
            context.contentResolver.openInputStream(capturedMemory.uri)?.use { it.readBytes() }
                ?: error("Could not read photo")

        val path = "$userId/${Uuid.random()}.jpg"

        return try {
            storage.from("memories").upload(path, bytes)
            val res = MemoryInsertDto(
                imagePath = path,
                caption = capturedMemory.caption,
                imgLatitude = lat,
                imgLongitude = lng,
                capturedAt = capturedMemory.capturedAt,
                place = placeName,
            )

            postgrest.from("memories_").insert(res)
            Result.success("Picture successfully uploaded")
        } catch (e: CancellationException) {
            storage.from("memories").delete(path)
            context.contentResolver.delete(capturedMemory.uri, null, null)
            throw e
        } catch (e: Exception) {
            storage.from("memories").delete(path)
            context.contentResolver.delete(capturedMemory.uri, null, null)
            Result.failure(e)
        }
    }

    override suspend fun DeleteImageFromDatabase(memoryId:String): Result<Unit> {
       TODO()
    }

    override suspend fun getMemoriesFromDatabase(): Result<List<Memories>> {
        return try {
            val dtos = postgrest.from("memories_").select().decodeList<MemoriesDto>()
            val  memories = dtos.map { dto ->
                val publicUrl = storage.from("memories").createSignedUrl(dto.imagePath,15.minutes)
                dto.copy(imageUrl = publicUrl).toDomain()
            }
            Result.success(memories)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
