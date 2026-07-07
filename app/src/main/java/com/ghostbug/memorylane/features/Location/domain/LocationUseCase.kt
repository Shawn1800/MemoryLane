package com.ghostbug.memorylane.features.Location.domain

import android.util.Log
import android.util.Log.e
import com.ghostbug.memorylane.features.Location.domain.model.LocationModel
import com.ghostbug.memorylane.features.Location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class LocationUseCase (
    private val locationRepository: LocationRepository
) {
     fun getLocation(): Flow<Result<LocationModel?>>  {
         return locationRepository.getLocation()
             .catch  { e ->
                 Log.e("LocationTracker","Error:${e.message}")
         }
     }

}