package com.ghostbug.memorylane.features.location.presentation

sealed class LocationEvent  {
    data object OnLocationButton : LocationEvent()
}