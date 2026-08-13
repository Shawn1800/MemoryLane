package com.ghostbug.memorylane.features.Location.domain.repository

import com.ghostbug.memorylane.features.Location.domain.model.Coordinates
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getCurrentLocation(): Flow<Coordinates?>

    suspend fun getLastKnownLocation() : Coordinates ?


}