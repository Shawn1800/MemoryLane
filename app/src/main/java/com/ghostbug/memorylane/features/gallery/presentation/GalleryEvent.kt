package com.ghostbug.memorylane.features.gallery.presentation

sealed class GalleryEvent {
    data object GetAllMemories : GalleryEvent()
}