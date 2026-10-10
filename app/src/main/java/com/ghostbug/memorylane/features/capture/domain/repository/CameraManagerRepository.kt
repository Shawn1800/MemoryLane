package com.ghostbug.memorylane.features.capture.domain.repository

import com.ghostbug.memorylane.features.capture.data.dto.MemoriesDto
import com.ghostbug.memorylane.features.capture.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.capture.domain.model.Memories

interface CameraManagerRepository{
    suspend fun SaveImageToDatabase(capturedMemory: CapturedMemory): Result<String>
    suspend fun DeleteImageFromDatabase(path:String): Result<Unit>
    suspend fun getMemoriesFromDatabase(): Result<List<MemoriesDto>>
}