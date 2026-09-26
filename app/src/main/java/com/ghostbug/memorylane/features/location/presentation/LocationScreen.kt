package com.ghostbug.memorylane.features.location.presentation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghostbug.memorylane.features.location.domain.model.Coordinates
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.Marker
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    locationViewModel: LocationViewModel = koinViewModel(),
    onBack: Unit
) {
    val state by locationViewModel.state.collectAsStateWithLifecycle()
    MapContent(
        longitude = state.longitude,
        latitude = state.latitude,
        loading = state.loading,
        onLocationClick = {locationViewModel.onEvent(LocationEvent.OnLocationButton)}
    )
}

@Composable
fun MapContent(
    longitude: Double?,
    latitude: Double?,
    loading: Boolean,
    onLocationClick: () -> Unit
) {
     Log.d(TAG,"MapContent12:lat=$latitude,long=$longitude")
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }
    // Whenever the ViewModel produces a new location, fly the camera to it.
    LaunchedEffect(longitude,latitude) {
        val lat = latitude
        val lng = longitude  // needs to eb changed
        if (lat != null && lng != null) {
            mapViewportState.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(lng, lat))
                    zoom(15.0)
                },
                mapAnimationOptions {
                    duration(3000)
                },
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            onMapClickListener = { clickedPoint ->  // Listen for taps
                // When user taps...
                mapViewportState.easeTo(    // Smoothly move camera to
                    cameraOptions {         // with these settings:
                        center(clickedPoint)  // Center on the tapped location
                    }
                )
                true  // Return true = "I handled the click"
            }
        ) {
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    enabled = true
                    puckBearing = PuckBearing.HEADING
                    puckBearingEnabled = true
                    pulsingEnabled=true
                    showAccuracyRing =true

                }
                mapViewportState.transitionToFollowPuckState()
            }
            val lat =latitude
            val lng =longitude
            if (lat != null && lng != null) {
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(Point.fromLngLat(lng ,lat))
                        allowOverlapWithPuck(true)
                    },

                ) {
                    ViewAnnotationContent()
                }

            }
        }
        FloatingActionButton(
            onClick = onLocationClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("LOC")
        }
    }
}

@Composable
 fun  ViewAnnotationContent() {
    Text (
        text = "Hello world dfgdfgd",
        modifier = Modifier
            .padding(3.dp)
            .width(100.dp)
            .height(60.dp)
            .background(
                Color.White
            ),
        textAlign = TextAlign.Center,
        fontSize = 12.sp
    )

}
