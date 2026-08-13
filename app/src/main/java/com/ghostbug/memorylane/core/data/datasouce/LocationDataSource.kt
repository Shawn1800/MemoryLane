package com.ghostbug.memorylane.core.data.datasouce

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.ghostbug.memorylane.features.Location.domain.model.Coordinates
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationDataSource {
    val locationService: LocationService = LocationServiceFactory.getOrCreate()
    val request =
        LocationProviderRequest.Builder()
            .interval(
                IntervalSettings.Builder()
                    .interval(0L)
                    .minimumInterval(0L)
                    .maximumInterval(0L)
                    .build()
            )
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build();

    fun getCurrentLocation(): Flow<Coordinates> = callbackFlow {
        val result = locationService.getDeviceLocationProvider(request)
        if (result.isError) {
            close()
            return@callbackFlow
        }
        val locationProvider = result.value
            ?: run {
                close()
                return@callbackFlow
            }

        if (result.isValue) {
            val locationObserver = object : LocationObserver {
                override fun onLocationUpdateReceived(locations: MutableList<Location?>) {

                    val latest = locations.lastOrNull() ?: return

                    val coordinates = Coordinates(
                        latitude = latest.latitude,
                        longitude = latest.longitude,
                        timestamp = latest.timestamp,
                        horizontalAccuracy = latest.horizontalAccuracy,
                        bearing = latest.bearing,
                        speed = latest.speed,
                        altitude = latest.altitude
                    )
                    trySend(coordinates)
                    Log.d(TAG, "Location update received: " + locations)
                }
            }

            locationProvider.addLocationObserver(locationObserver)

            awaitClose {
                locationProvider.removeLocationObserver(locationObserver)
            }
        }

    }

    suspend fun getLastKnownLocation(): Coordinates? = suspendCancellableCoroutine { continuation ->
        val result = locationService.getDeviceLocationProvider(request)

        if (result.isError || result.value == null) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val locationProvider = result.value!!
        locationProvider.getLastLocation { location ->
            val coordinates = location?.let {
                Coordinates(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    timestamp = it.timestamp,
                    horizontalAccuracy = it.horizontalAccuracy,
                    bearing = it.bearing,
                    speed = it.speed,
                    altitude = it.altitude
                )
            }
            continuation.resume(coordinates)
        }
    }

}