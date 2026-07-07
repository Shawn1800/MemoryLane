package com.ghostbug.memorylane.features.Location.presentation

import com.ghostbug.memorylane.features.Location.domain.model.LocationModel

data class LocationState (
    val loading: Boolean= false,
    val location: LocationModel? = null,
    val error: String? = null,
)
