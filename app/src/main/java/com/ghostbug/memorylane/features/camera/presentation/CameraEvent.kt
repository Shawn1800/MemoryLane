package com.ghostbug.memorylane.features.camera.presentation

import com.ghostbug.memorylane.features.camera.domain.cache.CapturedMemory

sealed interface CameraEvent {
    data class onUploadClicked(val capturedMemory: CapturedMemory): CameraEvent
}