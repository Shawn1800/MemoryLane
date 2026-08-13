package com.ghostbug.memorylane

import android.content.Context
import com.ghostbug.memorylane.core.data.datasouce.LocationDataSource
import com.ghostbug.memorylane.features.Location.data.repositoryImpl.LocationRepositoryImpl
import com.ghostbug.memorylane.features.Location.domain.repository.LocationRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {

    val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(LocationDataSource(context))
    }
}
