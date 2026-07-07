package com.ghostbug.memorylane.features.Location.data.repositoryImpl

import com.ghostbug.memorylane.core.data.datasouce.LocationDataSource
import com.ghostbug.memorylane.features.Location.domain.model.LocationModel
import com.ghostbug.memorylane.features.Location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource,   // injected, not returned
) : LocationRepository {

    override  fun getLocation(): Flow<Result<LocationModel?>> {
       return locationDataSource.getLocation()
    }
}
