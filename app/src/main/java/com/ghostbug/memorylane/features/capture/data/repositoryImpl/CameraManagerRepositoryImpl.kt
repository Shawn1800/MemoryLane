package com.ghostbug.memorylane.features.capture.data.repositoryImpl

import android.content.Context
import android.util.Log
import com.ghostbug.memorylane.features.capture.data.dto.MemoriesDto
import com.ghostbug.memorylane.features.capture.data.dto.MemoryInsertDto
import com.ghostbug.memorylane.features.capture.data.dto.toDomain
import com.ghostbug.memorylane.features.capture.data.repositoryImpl.utils.ReverseGeocoder
import com.ghostbug.memorylane.features.capture.data.repositoryImpl.utils.compressImage
import com.ghostbug.memorylane.features.capture.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import com.ghostbug.memorylane.features.capture.domain.repository.CameraManagerRepository
import com.mapbox.maps.extension.style.image.image
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.time.Duration.Companion.hours
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
                reverseGeocoder.reverseGeocode(lng,lat)
            } else "unknown location"

        Log.e("CameraManagerRepositoryImpl", "SaveImageToDatabase: $placeName")

        val bytes = compressImage(
            context = context,
            uri = capturedMemory.uri,
            maxDimension = 1920,
            quality = 85
        )

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

    override suspend fun getMemoriesFromDatabase(): Result<List<Memories>> = coroutineScope {
        try {
            // 1. Fetch memory metadata rows from Supabase database
            val dtos = postgrest.from("memories_").select().decodeList<MemoriesDto>()

            // 2. Generate Signed URLs in PARALLEL simultaneously across background threads
            val deferredMemories = dtos.map { dto ->
                async {
                    val signedUrl = try {
                        storage.from("memories").createSignedUrl(dto.imagePath, 1.hours)
                    } catch (e: Exception) {
                        android.util.Log.e(
                            "CameraManagerRepositoryImpl",
                            "Failed signed URL for ${dto.imagePath}", e
                        )
                        "" // If 1 file was deleted or invalid, don't fail the rest!
                    }
                    dto.copy(imageUrl = signedUrl).toDomain()
                }
            }
            // 3. Await all signed URLs and filter out any invalid/deleted files
            val memories = deferredMemories.awaitAll().filter { it.imageUrl.isNotBlank() }

            Result.success(memories)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMemory(memoryId: String): Result<Unit> {
        return try {
           val dto = postgrest.from("memories_")
               .select { filter { eq("id", memoryId) } }
               .decodeSingleOrNull<MemoriesDto>()

            if (dto != null ) {
                storage.from("memories").delete(dto.imagePath)
            }
            postgrest.from("memories_").delete {
                filter {
                    eq("id", memoryId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
