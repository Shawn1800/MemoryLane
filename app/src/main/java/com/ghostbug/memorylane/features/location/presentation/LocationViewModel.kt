package com.ghostbug.memorylane.features.location.presentation

import android.content.ContentValues.TAG
import android.util.Log
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
    init {
        onEvent(LocationEvent.OnLocationButton)
    }

    fun onEvent (event: LocationEvent) {
        when (event) {
            is LocationEvent.OnLocationButton -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                    val coordinates = locationUseCase()
                    Log.d(TAG,"coordinates: $coordinates")
                    _state.update {
                        it.copy(
                            loading = false,
                            longitude = coordinates?.longitude,
                            latitude = coordinates?.latitude
                        )
                    }

                }


            }
        }
    }

}