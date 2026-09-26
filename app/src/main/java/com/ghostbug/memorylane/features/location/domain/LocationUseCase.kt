package com.ghostbug.memorylane.features.location.domain

import com.ghostbug.memorylane.features.location.domain.model.Coordinates
import com.ghostbug.memorylane.features.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.firstOrNull

class LocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Coordinates? {

        val lastKnownLocation = locationRepository.getLastKnownLocation()

        if (lastKnownLocation != null) {
            return lastKnownLocation
        }

        return locationRepository.getCurrentLocation().firstOrNull()
    }
}
