package com.ghostbug.memorylane.features.camera.domain.repository

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController

interface repository {
    suspend fun  takePhoto(controller: CameraController)

}