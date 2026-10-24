package com.ghostbug.memorylane.features.gallery.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbug.memorylane.features.capture.data.dto.toDomain
import com.ghostbug.memorylane.features.capture.domain.cache.toDomain
import com.ghostbug.memorylane.features.capture.domain.repository.CameraManagerRepository
import com.ghostbug.memorylane.features.capture.presentation.CameraEvent
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
                if (_state.value.memories.isNotEmpty()) return
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

            is GalleryEvent.onDeleteMemory -> {
                viewModelScope.launch {
                    _state.update { it.copy(loading = true) }
                    cameraManagerRepository.deleteMemory(event.memoryId)
                        .onSuccess {
                            _state.update { state ->
                                state.copy(
                                    loading = false,
                                    memories = state.memories.filter { it.id != event.memoryId } // Remove deleted memory!
                                )
                            }
                        }
                        .onFailure { e ->
                            _state.update {
                                it.copy(
                                    loading = false,
                                    error = e.message ?: "Failed to delete"
                                )
                            }
                        }
                }
            }
            is GalleryEvent.GetSingleMemory -> {
                _state.update {
                    it.copy(
                        memories = it.memories + event.newMemory.toDomain()
                    )
                }
                }
            }
        }
    }
