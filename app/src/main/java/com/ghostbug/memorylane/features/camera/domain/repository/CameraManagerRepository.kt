package com.ghostbug.memorylane.features.camera.domain.repository

import com.ghostbug.memorylane.features.camera.domain.cache.CapturedMemory

interface CameraManagerRepository{
    suspend fun SaveImageToDatabase(capturedMemory: CapturedMemory): Result<Unit>
//    suspend fun DeleteImageFromDatabase(): Boolean
//    suspend fun getImageFromDataBase(): Boolean
}