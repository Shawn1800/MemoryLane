package com.ghostbug.memorylane.features.capture.data.repositoryImpl.utils




import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import io.ktor.utils.io.CancellationException


class ReverseGeocoder {
    val placeAutocomplete = PlaceAutocomplete.create()
    suspend fun reverseGeocode(long: Double, lat: Double) :String? {
        Log.d("ReverseGeocoder", "reverseGeocode: $long, $lat")
        return try {
            val response = placeAutocomplete.reverse(
                point = Point.fromLngLat(long, lat)
            )
            val best = response.value?.firstOrNull()
            best?.name?:"Unknown Location"
        } catch (e: CancellationException) {
            throw e
        }
        catch (e: Exception) {
            Log.e("ReverseGeocoder", "Exception during reverse geocoding", e)
            "Error during reverse geocoding"
        }
    }
}