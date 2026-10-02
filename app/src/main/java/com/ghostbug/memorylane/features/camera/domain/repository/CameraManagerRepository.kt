package com.ghostbug.memorylane.features.camera.domain.repository

interface CameraManagerRepository{
    suspend fun launchCamera()
    suspend fun takePicture()
    suspend fun saveResultToDataBase()
}