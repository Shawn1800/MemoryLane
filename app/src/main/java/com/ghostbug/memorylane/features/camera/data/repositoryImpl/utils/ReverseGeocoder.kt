package com.ghostbug.memorylane.features.camera.data.repositoryImpl.utils




import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete


class ReverseGeocoder {
    val placeAutocomplete = PlaceAutocomplete.create()
    suspend fun reverseGeocode(long: Double, lat: Double) :String {
        Log.d("ReverseGeocoder", "reverseGeocode: $long, $lat")
        return try {
            val response = placeAutocomplete.reverse(
                point = Point.fromLngLat(long, lat)
            )
            if (response.isError && response.value == null) {
                throw Exception("Something went wrong with reverse geocoding")
            }
            val best = response.value?.firstOrNull()
            return best?.name?:"Unknown Location"
        } catch (e: Exception) {
            Log.e("ReverseGeocoder", "Exception during reverse geocoding", e)
            "Unknown Location"
        }
    }
}