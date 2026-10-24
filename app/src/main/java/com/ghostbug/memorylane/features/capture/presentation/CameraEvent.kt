package com.ghostbug.memorylane.features.capture.presentation

import com.ghostbug.memorylane.features.capture.domain.cache.CapturedMemory

sealed interface CameraEvent {
    data class onUploadClicked(val capturedMemory: CapturedMemory): CameraEvent
    data object  getAllMemories  : CameraEvent
    data class  onCaptionChanged(val caption:String):CameraEvent


}