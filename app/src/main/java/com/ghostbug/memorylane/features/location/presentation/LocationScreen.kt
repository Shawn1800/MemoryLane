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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ghostbug.memorylane.core.permissions.PermissionScreen
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import com.ghostbug.memorylane.features.gallery.presentation.GalleryEvent
import com.ghostbug.memorylane.features.gallery.presentation.GalleryState
import com.ghostbug.memorylane.features.gallery.presentation.GalleryViewModel
import com.ghostbug.memorylane.features.location.domain.model.Coordinates
import com.ghostbug.memorylane.features.profile.presentation.ProfileUiEvent
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
    onBack: () -> Unit,

) {

    val permissionState by permissionsViewModel.permissionState.collectAsStateWithLifecycle()
    val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()

//    LaunchedEffect(Unit) {
//        permissionsViewModel.checkPermissions()

//    }
    LaunchedEffect(Unit) {
        galleryViewModel.onEvent(GalleryEvent.GetAllMemories)
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
            onDelete = { memoryId ->
                galleryViewModel.onEvent(GalleryEvent.onDeleteMemory(memoryId))
            },
            galleryState = GalleryState(),
            memories = galleryState.memories,
            onBack = onBack,
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
    memories: List<Memories>,
    galleryState: GalleryState,
    onDelete : (String) -> Unit,
    onBack: () -> Unit,
) {
    // 1. State for currently selected memory tapped on map
    var selectedMemory by remember { mutableStateOf<Memories?>(null) }

    LaunchedEffect(memories) {
        if (selectedMemory != null && memories.none { it.id == selectedMemory?.id }) {
            selectedMemory = null
        }
    }



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
//    LaunchedEffect(longitude, latitude) {
//        Log.d(TAG, "MapContent12:lat=$latitude,long=$longitude")
//        val lat = latitude
//        val lng = longitude  // needs to eb changed
//        if (lat != null && lng != null) {
////            mapViewportState.flyTo(
////                cameraOptions {
////                    center(Point.fromLngLat(lng,lat ))
////                    zoom(15.0)
////                },
//                mapAnimationOptions {
//                    duration(3000)
//                }
////            )
//        }
//    }

    val located = remember(memories) {
        memories.filter { it.imgLatitude != null && it.imgLongitude != null }
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            // 1. Hide Compass
            compass = {},
            // 2. Hide Scale/Zoom Bar
            scaleBar = {},
            // 3. Hide Mapbox Logo
            logo = {},
            // 4. Hide Attribution ('i' info button)
            attribution = {},
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
                    locationPuck = createDefault2DPuck(withBearing = false)
                    enabled = true
                    puckBearing = PuckBearing.COURSE
                    puckBearingEnabled = true
                    pulsingEnabled = false
                    showAccuracyRing = true
                }
                mapViewportState.transitionToFollowPuckState(
                    FollowPuckViewportStateOptions.Builder()
                        .zoom(15.0)
                        .bearing(null)
                        .build()
                )
            }

            Log.d("LocationScreen", "Rendering ${memories.size} memories on map: $memories")
            located.forEach { memory ->
                val lat = memory.imgLatitude
                val lng = memory.imgLongitude
                val isSelected = selectedMemory?.id == memory.id
                if (lat != null && lng != null) {
                    ViewAnnotation(
                        options = viewAnnotationOptions {
                            geometry(Point.fromLngLat(lng, lat))
                            allowOverlapWithPuck(true)
                            annotationAnchor {
                                anchor(ViewAnnotationAnchor.BOTTOM)
                                offsetX(-10.0)
                                offsetY(20.0)
                            }
                            priority(if (isSelected)100 else 10 )
                            minZoom(7f)
                            allowZElevate(true)
                            allowOverlap(true)

                        },
                    ) {
                        MemoryMarker(
                            photoUrl = memory.imageUrl,
                            imagePath=memory.imagePath,
                            isSelected = selectedMemory?.id == memory.id,
                            dateLabel = memory.place,
                            onClick = {
                                selectedMemory = memory
                            }
                        )
                    }

                }
            }
        }
        SmallFloatingActionButton(
            onClick = {
                // 1. Instantly center map on user's location puck
                mapViewportState.transitionToFollowPuckState(
                    FollowPuckViewportStateOptions.Builder()
                        .zoom(15.0)
                        .build()
                )
                // 2. Fetch fresh location coordinates in ViewModel
                onLocationClick()
            },
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 16.dp) // Positioned neatly above the bottom nav bar
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location",
                tint = Color(0xFF1F1F1F),
                modifier = Modifier.size(22.dp)
            )
        }
        // 3. Render Modal Bottom Sheet when a memory is tapped
        ImageViewerFromLocationSheet(
            onDelete = onDelete,
            memory = selectedMemory,
            state = galleryState,
            onDismiss = { selectedMemory = null },
        )
    }
}

@Composable
fun MemoryMarker(
    photoUrl: String,
    imagePath: String? = null,
    isSelected: Boolean = false,
    dateLabel: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "markerScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 4.dp,
        label = "markerElevation"
    )
    val imageRequest = remember(photoUrl,imagePath,context) {
        ImageRequest.Builder(context)
            .data(photoUrl)
            .apply {
                if (!imagePath.isNullOrBlank()) {
                    memoryCacheKey(imagePath)
                    diskCacheKey(imagePath)
                }
            }
            .crossfade(true)
            .build()

    }

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
                       model = imageRequest,

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

