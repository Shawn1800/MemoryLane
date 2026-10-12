package com.ghostbug.memorylane.features.gallery.presentation

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

class GalleryViewModel(
    private val cameraManagerRepository: CameraManagerRepository
): ViewModel() {
    private val _state = MutableStateFlow(GalleryState())
    val state: StateFlow<GalleryState> = _state.asStateFlow()

    private  val _uiEvent  = MutableSharedFlow<GalleryUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        onEvent(GalleryEvent.GetAllMemories)
    }

    fun onEvent(event: GalleryEvent) {
        when (event) {
            is GalleryEvent.GetAllMemories -> {
                viewModelScope.launch {
                _state.update {
                    it.copy(
                        loading = true,
                        error = null
                    )
                }
                    cameraManagerRepository.getMemoriesFromDatabase()
                        .onSuccess { memories ->
                            _state.update {
                                it.copy(
                                    loading = false,
                                    memories = memories
                                )
                            }
                        }
                        .onFailure {
                            _state.update {
                                it.copy(
                                    loading = false,
                                    error = "failed to get memories"
                                )
                            }
                        }
                }
            }
        }
    }
}