package com.ghostbug.memorylane.features.Location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbug.memorylane.features.Location.domain.LocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class LocationViewModel (
    private val locationUseCase: LocationUseCase
): ViewModel() {
   private val _state = MutableStateFlow(LocationState())
    val state: StateFlow<LocationState> = _state.asStateFlow()

    fun onEvent (event: LocationEvent) {
        when (event) {
            is LocationEvent.OnLocationButton -> {
                _state.update { it.copy(loading = true) }
                locationUseCase.getLocation()
                    .onEach { result ->
                        result.fold(
                            onSuccess = { model ->
                                _state.update { it.copy(loading = false, location = model, error = null) }
                            },
                            onFailure = { e ->
                                _state.update { it.copy(loading = false, error = e.message) }
                            },
                        )
                    }
                    .launchIn(viewModelScope)   // ← THIS is what actually starts the flow
            }
        }
    }

}