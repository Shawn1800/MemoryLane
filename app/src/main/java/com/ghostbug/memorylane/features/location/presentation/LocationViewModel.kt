package com.ghostbug.memorylane.features.location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbug.memorylane.features.location.domain.LocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationViewModel (
    private val locationUseCase: LocationUseCase
): ViewModel() {
   private val _state = MutableStateFlow(LocationState())
    val state: StateFlow<LocationState> = _state.asStateFlow()

    fun onEvent (event: LocationEvent) {
        when (event) {
            is LocationEvent.OnLocationButton -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                    val location = locationUseCase()
                    _state.update {
                        it.copy(
                            loading = false,
                            location = location,
                        )
                    }

                }


            }
        }
    }

}