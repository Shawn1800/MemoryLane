package com.ghostbug.memorylane.features.camera.data.repositoryImpl

import com.ghostbug.memorylane.features.camera.data.dto.MemoryDto
import com.ghostbug.memorylane.features.camera.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.camera.domain.model.Memory
import com.ghostbug.memorylane.features.camera.domain.repository.CameraManagerRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.api.AuthenticatedApiConfig
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.supabaseJson
import kotlin.time.Clock

//
//class CameraManagerRepositoryImpl(
//    private val auth: Auth,
//    private val postgrest: Postgrest,
//) : CameraManagerRepository{
//
//   override suspend fun SaveImageToDatabase (capturedMemory: CapturedMemory):Boolean {
//
//
//       val user = auth.currentSessionOrNull()?.user
//       val  userId =  user?.id
//       val  res = Memory(
//           id="",
//           userId = userId.toString(),
//           imagePath = capturedMemory.uri.toString(),
//           caption = capturedMemory.caption,
//           imgLatitude = capturedMemory.img_latitude!!,
//           imgLongitude = capturedMemory.img_longitude!!,
//           capturedAt = capturedMemory.capturedAt,
//           createdAt = Clock.System.now(),
//           place = ,
//
//       )
//    }
//    override suspend fun DeleteImageFromDatabase (): Boolean {
//
//    }
//    override suspend fun getImageFromDataBase (): Boolean {
//
//    }
//
//    }
