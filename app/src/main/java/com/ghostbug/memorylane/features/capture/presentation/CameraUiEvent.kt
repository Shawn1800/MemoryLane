package com.ghostbug.memorylane.features.capture.presentation

sealed interface CameraUiEvent {
    data object navToLocationScreen : CameraUiEvent
}

