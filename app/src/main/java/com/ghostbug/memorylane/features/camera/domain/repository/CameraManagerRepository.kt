package com.ghostbug.memorylane.features.camera.domain.repository

interface CameraManagerRepository{
    suspend fun SaveImageToDatabase(): Boolean
    suspend fun DeleteImageFromDatabase(): Boolean
    suspend fun getImageFromDataBase(): Boolean
}