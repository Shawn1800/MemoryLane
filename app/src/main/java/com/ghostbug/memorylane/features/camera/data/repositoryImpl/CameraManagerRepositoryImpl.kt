package com.ghostbug.memorylane.features.camera.data.repositoryImpl

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.ghostbug.memorylane.features.camera.data.dto.MemoriesDto
import com.ghostbug.memorylane.features.camera.data.dto.MemoryInsertDto
import com.ghostbug.memorylane.features.camera.data.repositoryImpl.utils.ReverseGeocoder
import com.ghostbug.memorylane.features.camera.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.camera.domain.repository.CameraManagerRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class CameraManagerRepositoryImpl(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val reverseGeocoder: ReverseGeocoder,
    private val context : Context,
    private val storage: Storage
) : CameraManagerRepository{

   @OptIn(ExperimentalUuidApi::class)
   override suspend fun SaveImageToDatabase (capturedMemory: CapturedMemory):Result<Unit> = runCatching {

       val lat = capturedMemory.imgLatitude
       val lng = capturedMemory.imgLongitude

       // place name is nice-to-have: never block the save on it
       val placeName = if (lat != null && lng != null) {
           runCatching { reverseGeocoder.reverseGeocode(lat, lng) }.getOrNull()
       } else null

       Log.e("CameraManagerRepositoryImpl", "SaveImageToDatabase: $placeName")

       val user = auth.currentSessionOrNull()?.user
       val  userId =  user?.id


       val bytes = context.contentResolver.openInputStream(capturedMemory.uri)?.use { it.readBytes() }
               ?: error("Could not read photo")

       val path =  "$userId/${Uuid.random()}.jpg"
       storage.from("memories").upload(path, bytes)
        try {
           val res = MemoryInsertDto(
               userId = userId.toString(),
               imagePath = path,
               caption = capturedMemory.caption,
               imgLatitude = lat,
               imgLongitude = lng,
               capturedAt = capturedMemory.capturedAt,
               place = placeName,
           )
           postgrest.from("memories_").insert(res)
            Result.success("Picture successfully uploaded")
       } catch (e: Exception) {
           runCatching { storage.from("memories").delete(path) }
           throw e
       }

    }
//    override suspend fun DeleteImageFromDatabase (): Boolean {
//
//    }
//    override suspend fun getImageFromDataBase (): Boolean {
//
//    }

    }
