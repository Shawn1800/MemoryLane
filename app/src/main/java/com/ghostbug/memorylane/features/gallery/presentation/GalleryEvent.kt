package com.ghostbug.memorylane.features.gallery.presentation

import com.ghostbug.memorylane.features.capture.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import com.ghostbug.memorylane.features.capture.presentation.CameraEvent

sealed class GalleryEvent {
    data object GetAllMemories : GalleryEvent()

    data class  onDeleteMemory(val memoryId :String) : GalleryEvent()
    data class GetSingleMemory(val newMemory: CapturedMemory) : GalleryEvent()

}