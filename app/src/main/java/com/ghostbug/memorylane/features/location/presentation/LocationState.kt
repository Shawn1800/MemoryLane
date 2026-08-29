package com.ghostbug.memorylane.features.location.presentation

import com.ghostbug.memorylane.features.location.domain.model.Coordinates


data class LocationState (
    val loading: Boolean= false,
    val location: Coordinates? = null,
    val error: String? = null,
)
