package com.ghostbug.memorylane.features.location.domain.repository

import com.ghostbug.memorylane.features.location.domain.model.Coordinates
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getCurrentLocation(): Flow<Coordinates?>

    suspend fun getLastKnownLocation() : Coordinates ?


}