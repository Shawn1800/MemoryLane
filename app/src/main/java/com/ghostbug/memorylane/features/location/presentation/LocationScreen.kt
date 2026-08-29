package com.ghostbug.memorylane.features.location.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghostbug.memorylane.features.location.domain.model.Coordinates
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    locationViewModel: LocationViewModel = koinViewModel()
) {
    val state by locationViewModel.state.collectAsStateWithLifecycle()
    MapContent(
        location = state.location,
        loading = state.loading,
        onLocationClick = {locationViewModel.onEvent(LocationEvent.OnLocationButton)}
    )
}

@Composable
fun MapContent(
    location: Coordinates?,
    loading: Boolean,
    onLocationClick: () -> Unit
) {

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }


    // Whenever the ViewModel produces a new location, fly the camera to it.
    LaunchedEffect(location) {
        val lat = location?.latitude
        val lng = location?.longitude  // needs to eb changed
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


