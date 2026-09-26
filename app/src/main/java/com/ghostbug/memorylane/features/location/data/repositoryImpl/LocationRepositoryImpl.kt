package com.ghostbug.memorylane.features.location.data.repositoryImpl

import com.ghostbug.memorylane.core.data.datasouce.LocationDataSource
import com.ghostbug.memorylane.features.location.domain.model.Coordinates
import com.ghostbug.memorylane.features.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow


class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource,   // injected, not returned
) : LocationRepository {

    override   fun getCurrentLocation(): Flow<Coordinates> {
        return locationDataSource.getCurrentLocation()
    }

    override suspend fun getLastKnownLocation(): Coordinates? {
        return locationDataSource.getLastKnownLocation()
    }
}
