package com.ghostbug.memorylane.features.Location.domain.repository

import com.ghostbug.memorylane.features.Location.domain.model.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocation(): Flow<Result<LocationModel?>>
//    suspend fun requestLocationPermission(): Boolean
//    suspend fun isLocationPermissionGranted(): Boolean

}