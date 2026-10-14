package com.ghostbug.memorylane.features.capture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbug.memorylane.features.capture.domain.repository.CameraManagerRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val cameraManagerRepository: CameraManagerRepository,
): ViewModel() {
    private val _state  = MutableStateFlow(CameraState())
    val state : StateFlow<CameraState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<CameraEvent>()
    val event = _event.asSharedFlow()

    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.onUploadClicked ->  {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                    cameraManagerRepository.SaveImageToDatabase(event.capturedMemory)
                    _state.update {
                        it.copy(
                            loading = false
                        )
                    }
                }
            }
            is CameraEvent.getAllMemories -> TODO()

        }
    }




}