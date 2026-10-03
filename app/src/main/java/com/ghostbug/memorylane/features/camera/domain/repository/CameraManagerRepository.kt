package com.ghostbug.memorylane.features.camera.domain.repository

interface CameraManagerRepository{
    suspend fun SaveImageToDatabase()
    suspend fun DeleteImageFromDatabase()
    suspend fun getImageFromDataBase()
}