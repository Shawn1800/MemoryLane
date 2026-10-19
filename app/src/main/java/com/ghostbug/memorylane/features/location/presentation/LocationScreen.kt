package com.ghostbug.memorylane.features.location.presentation

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.ContentValues.TAG
import android.graphics.drawable.Icon
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
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
import coil3.compose.AsyncImage
import com.ghostbug.memorylane.core.permissions.PermissionScreen
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import com.ghostbug.memorylane.features.gallery.presentation.GalleryEvent
import com.ghostbug.memorylane.features.gallery.presentation.GalleryViewModel
import com.ghostbug.memorylane.features.location.domain.model.Coordinates
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.Point
import com.mapbox.maps.ViewAnnotationAnchor
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
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import com.mapbox.maps.viewannotation.annotationAnchor
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
    permissionsViewModel: PermissionsViewModel = koinViewModel(),
    galleryViewModel: GalleryViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val permissionState by permissionsViewModel.permissionState.collectAsStateWithLifecycle()
    val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        permissionsViewModel.checkPermissions()
//        galleryViewModel.onEvent(GalleryEvent.GetAllMemories)
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
                locationViewModel.onEvent(LocationEvent.OnLocationButton)
            },
            memories = galleryState.memories

        )
    } else {
        PermissionScreen(
            permissionsViewModel = permissionsViewModel,
            onNavigate = {},
            uiEvent = permissionsViewModel.uiEvent
        )
    }
}

@Composable
fun MapContent(
    longitude: Double?,
    latitude: Double?,
    loading: Boolean,
    onLocationClick: () -> Unit,
    memories: List<Memories>
) {
    Log.d(TAG, "MapContent12:lat=$latitude,long=$longitude")
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }
    // Whenever the ViewModel produces a new location, fly the camera to it.
    LaunchedEffect(longitude, latitude) {
        Log.d(TAG, "MapContent12:lat=$latitude,long=$longitude")
        val lat = latitude
        val lng = longitude  // needs to eb changed
        if (lat != null && lng != null) {
            mapViewportState.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(lng,lat ))
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
                    })
                true  // Return true = "I handled the click"
            }) {
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    enabled = true
                    puckBearing = PuckBearing.HEADING
                    puckBearingEnabled = true
                    pulsingEnabled = true
                    showAccuracyRing = true

                }
                mapViewportState.transitionToFollowPuckState(
                    FollowPuckViewportStateOptions.Builder()
                        .zoom(15.0)
                        .build()
                )
            }
            memories.forEach { memory ->
                val lat = memory.imgLatitude
                val lng = memory.imgLongitude
                if (lat != null && lng != null) {
                    ViewAnnotation(
                        options = viewAnnotationOptions {
                            geometry(Point.fromLngLat(lng, lat))
                            allowOverlapWithPuck(true)
                            annotationAnchor {
                                anchor(ViewAnnotationAnchor.BOTTOM)
                            }
                            priority(10)
                            allowOverlap(true)
                            ignoreCameraPadding(false)
                            allowZElevate(true)
                            minZoom(10f)

                        },
                    ) {
                        MemoryMarker(
                            photoUrl = memory.imageUrl,
                            isSelected = true,
                            onClick = {}
                        )
                    }

                }
            }
        }
        FloatingActionButton(
            onClick = onLocationClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp),
            containerColor = Color(0xFF1B6E1B),
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "location",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MemoryMarker(
    photoUrl: String,
    isSelected: Boolean = false,
    dateLabel: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "markerScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 4.dp,
        label = "markerElevation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Optional date chip (only when selected)
        if (isSelected && !dateLabel.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.78f),
                shadowElevation = 2.dp,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = dateLabel,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Main photo card
        Surface(
            shape = RoundedCornerShape(14.dp),
            shadowElevation = elevation,
            color = Color.White,
            border = BorderStroke(
                width = if (isSelected) 2.5.dp else 1.5.dp,
                color = if (isSelected) Color(0xFF1B6E1B) else Color.White
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(if (isSelected) 64.dp else 52.dp)
            ) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp)
                        .clip(RoundedCornerShape(11.dp))
                )
            }
        }

        // Elegant pointer / tail
        Canvas(
            modifier = Modifier
                .size(width = 16.dp, height = 9.dp)
                .offset(y = (-1).dp)
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(
                path = path,
                color = if (isSelected) Color(0xFF1B6E1B) else Color.White
            )
            // subtle bottom shadow line
            drawPath(
                path = path,
                color = Color.Black.copy(alpha = 0.12f),
                style = Stroke(width = 1.5f)
            )
        }
    }
}

