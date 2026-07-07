package com.ghostbug.memorylane.features.Location.presentation

sealed class LocationEvent  {
    data object OnLocationButton : LocationEvent()
}