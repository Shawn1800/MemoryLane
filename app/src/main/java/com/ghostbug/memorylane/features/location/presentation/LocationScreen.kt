package com.ghostbug.memorylane.features.location.presentation

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghostbug.memorylane.core.permissions.PermissionScreen
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.io.path.Path
import kotlin.io.path.moveTo

private object NeoMarkerColors {
    val cream = Color(0xFFF7F1E3)
    val black = Color(0xFF141414)
    val peach = Color(0xFFF3A989)
    val mintGreen = Color(0xFFA9D6B0)
    val gold = Color(0xFFF0C14E)
}


private val markerShape: Shape = RoundedCornerShape(14.dp)
private val shadowOffset = 4.dp

@Composable
fun MapScreen(
    locationViewModel: LocationViewModel = koinViewModel(),
    permissionsViewModel: PermissionsViewModel= koinViewModel(),
    onBack: ()-> Unit
) {
    val permissionState by permissionsViewModel.permissionState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        permissionsViewModel.checkPermissions()
    }

    if (permissionState.hasLocationAccess) {
        val state by locationViewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            locationViewModel.onEvent(LocationEvent.OnLocationButton)
        }

        MapContent(
            longitude = state.longitude,
            latitude = state.latitude,
            loading = state.loading,
            onLocationClick = {
                locationViewModel.onEvent(LocationEvent.OnLocationButton)}
        )
    } else {
        PermissionScreen(
            permissionsViewModel =  permissionsViewModel,
            onNavigate = {
            },
            uiEvent = permissionsViewModel.uiEvent
        )
    }
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
//                    ViewAnnotationContent()
                    MemoryMapMarker(photoUrl = "", isSelected = true, onClick = {})
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
fun MemoryMapMarker(
    photoUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dateLabel: String? = null,
) {
    val chromeSize by animateDpAsState(
        targetValue = if (isSelected) 68.dp else 46.dp,
        label = "markerSize"
    )
    val frameColor by animateColorAsState(
        targetValue = if (isSelected) NeoMarkerColors.mintGreen else NeoMarkerColors.peach,
        label = "markerFrame"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSelected && dateLabel != null) {
            Box(
                modifier = Modifier
                    .offset(x = 2.dp, y = 2.dp)
                    .background(NeoMarkerColors.black, RoundedCornerShape(6.dp))
            ) {
                Text(
                    text = dateLabel,
                    color = NeoMarkerColors.cream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .offset(x = (-2).dp, y = (-2).dp)
                        .background(NeoMarkerColors.black, RoundedCornerShape(6.dp))
                        .border(2.dp, NeoMarkerColors.black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.padding(top = 4.dp))
        }

        // Photo chip: hard offset shadow behind + thick-bordered fill on top.
        Box {
            Box(
                modifier = Modifier
                    .offset(x = shadowOffset, y = shadowOffset)
                    .size(chromeSize)
                    .clip(markerShape)
                    .background(NeoMarkerColors.black)
            )
            Box(
                modifier = Modifier
                    .size(chromeSize)
                    .clip(markerShape)
                    .background(frameColor)
                    .border(2.5.dp, NeoMarkerColors.black, markerShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                    .padding(4.dp)
            ) {
//                AsyncImage(
//                    model = photoUrl,
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(chromeSize - 8.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                )
            }
        }

        // Tail — shadow triangle behind, bordered fill triangle on top.
        Canvas(modifier = Modifier.size(width = 18.dp, height = 10.dp)) {
            val tail = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            translate(left = shadowOffset.toPx() * 0.6f, top = shadowOffset.toPx() * 0.6f) {
                drawPath(tail, color = NeoMarkerColors.black)
            }
            drawPath(tail, color = frameColor)
            drawPath(tail, color = NeoMarkerColors.black, style = Stroke(width = 3f))
        }

        // Exact anchor point — golden accent, black outline.
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(NeoMarkerColors.gold, CircleShape)
                .border(1.5.dp, NeoMarkerColors.black, CircleShape)
        )
    }
}


