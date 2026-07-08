package com.ghostbug.memorylane.core.data.datasouce

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.ghostbug.memorylane.features.Location.domain.model.LocationModel
import android.util.Log
import androidx.core.app.ActivityCompat
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
import kotlinx.coroutines.flow.flow

class LocationDataSource (
   private val  context: Context
) {

//    suspend fun requestPermission(): Boolean


//    fun getLocation(): Flow<Result<LocationModel?>> {
//
//        val hasPermission = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//
//        return if (hasPermission) {
//            findLocation()
//        } else if (ContextCompat.shouldShowRequestPermissionRationale(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) || ActivityCompat.shouldShowRequestPermissionRationale(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        ) {
//
//            PermissionUtils.RationaleDialog.newInstance(
//                LOCATION_PERMISSION_REQUEST_CODE, true
//            ).show(supportFragmentManager, "dialog")
//            return
//        } else {
//            flow {
//                emit(Result.failure(Exception("Location permission not granted. Request it in the UI.")))
//            }
//        }
//    }

    @SuppressLint("RestrictedApi")
    fun findLocation(): Flow<Result<LocationModel>> = callbackFlow {
        val locationService: LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null

        val request = LocationProviderRequest.Builder()
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

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value!!

            val locationObserver = object : LocationObserver {
                override fun onLocationUpdateReceived(locations: MutableList<Location?>) {

                    val latest = locations.lastOrNull() ?: return

                    val model = LocationModel(
                        latitude = latest.latitude,
                        longitude = latest.longitude,
                        timestamp = latest.timestamp,
                        horizontalAccuracy = latest.horizontalAccuracy,
                        bearing = latest.bearing,
                        speed = latest.speed,
                        altitude = latest.altitude
                    )

                    trySend(Result.success(model))
                    Log.d(TAG, "Location update received: " + locations)
                }
            }

            locationProvider.addLocationObserver(locationObserver)

            awaitClose {
                locationProvider.removeLocationObserver(locationObserver)
            }

        } else {
            // Mapbox failed to initialize the provider
            trySend(Result.failure(Exception("Mapbox Error: ${result.error?.message}")))
            close()
        }
    }
}