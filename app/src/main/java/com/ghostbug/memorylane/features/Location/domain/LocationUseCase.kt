package com.ghostbug.memorylane.features.Location.domain

import android.util.Log
import android.util.Log.e
import com.ghostbug.memorylane.features.Location.domain.model.Coordinates
import com.ghostbug.memorylane.features.Location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class LocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Coordinates? {

        val lastKnownLocation =
            locationRepository.getLastKnownLocation()

        if (lastKnownLocation != null) {
            return lastKnownLocation
        }

        return locationRepository
            .getCurrentLocation()
            .firstOrNull()
    }
}
