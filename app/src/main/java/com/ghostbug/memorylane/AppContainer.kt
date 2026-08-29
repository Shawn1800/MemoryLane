package com.ghostbug.memorylane

import android.content.Context
import com.ghostbug.memorylane.core.data.datasouce.LocationDataSource
import com.ghostbug.memorylane.features.location.data.repositoryImpl.LocationRepositoryImpl
import com.ghostbug.memorylane.features.location.domain.repository.LocationRepository

class AppContainer(private val context: Context) {

    val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(LocationDataSource())
    }
}
