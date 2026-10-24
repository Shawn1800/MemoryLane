package com.ghostbug.memorylane.features.capture.presentation


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
import com.ghostbug.memorylane.features.capture.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.capture.domain.model.Memories
import com.ghostbug.memorylane.features.gallery.presentation.GalleryEvent
import com.ghostbug.memorylane.features.gallery.presentation.GalleryViewModel
import com.ghostbug.memorylane.features.location.presentation.LocationEvent
import com.ghostbug.memorylane.features.location.presentation.LocationViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import kotlin.let
import kotlin.time.Clock


@Composable
fun CameraScreen(
    onCaptured:(CapturedMemory)->Unit,
    locationViewModel: LocationViewModel = koinViewModel(),
    cameraViewModel: CameraViewModel = koinViewModel(),
    galleryViewModel: GalleryViewModel = koinViewModel(),
    modifier: Modifier,
    onBack: () -> Unit
) {
    val cameraState by cameraViewModel.state.collectAsStateWithLifecycle()
    val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()
    val locationState by locationViewModel.state.collectAsStateWithLifecycle()
    var pendingUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var capturedUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var capturedMemory by remember { mutableStateOf<CapturedMemory?>(null) }


    // 1. Fetch fresh GPS coordinates as soon as CameraScreen opens!
    LaunchedEffect(Unit) {
        locationViewModel.onEvent(LocationEvent.OnLocationButton)
    }

    LaunchedEffect(Unit) {
        cameraViewModel.event.collect {
            when (it) {
                is CameraUiEvent.navToLocationScreen -> {

                    capturedUri = null
                    pendingUri = null
                    capturedMemory = null
                    onBack()
            }}
        }
    }


    val context = LocalContext.current

    val  cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { pictureTaken->
        val uri = pendingUri
        if (pictureTaken && uri!=null) {
            capturedUri = uri
           val memory  =  CapturedMemory(
                uri=uri,
                imgLatitude = locationState.latitude,
                imgLongitude = locationState.longitude,
                capturedAt = Clock.System.now(),
                caption = cameraState.caption
            )
            capturedMemory = memory
            onCaptured(memory)
        } else {
            capturedUri = null
            pendingUri = null
            onBack()
        }
    }
    fun launchCamera() {
        val file = File.createTempFile("memory_",".jpeg", context.cacheDir)
        val uri  = FileProvider.getUriForFile(context,"${context.packageName}.fileProvider",file)
        pendingUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(Unit) {
        if (capturedUri == null && pendingUri == null) {
            launchCamera()
        }
    }

    if (capturedUri!=null) {
        DisplayImageAfterClickScreen(
            onBack = {
                // 1. Delete local temporary JPEG file from device cache
                capturedUri?.path?.let { path ->
                    runCatching { File(path).delete() }
                }
                onBack()
            },
            imageUri = capturedUri,
            modifier = modifier,
            caption = cameraState.caption,
            isUploading = cameraState.loading,
            onCaptionChanged = { newCaption ->
                cameraViewModel.onEvent(CameraEvent.onCaptionChanged(newCaption))},

            onUpload = {  capturedMemory?.let { memory ->
                val updatedMemory = memory.copy(
                    caption = cameraState.caption,
                    imgLatitude = memory.imgLatitude?:locationState.latitude,
                    imgLongitude = memory.imgLongitude?:locationState.longitude,
                    placeName = memory.placeName
                )

                // 1. Upload to Supabase in background
                cameraViewModel.onEvent(CameraEvent.onUploadClicked(updatedMemory))

                // 2. Instantly append to Map / Gallery state for 0ms rendering
                galleryViewModel.onEvent(GalleryEvent.GetSingleMemory(updatedMemory))
                }
//                capturedUri?.path?.let { path ->
//                    runCatching { File(path).delete() }
//                }

            },
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { launchCamera() } // still take the photo if denied; lat/lng will be null


//    Button(onClick = {
//        val granted = ContextCompat.checkSelfPermission(
//            context, Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//        if (granted) launchCamera()
//        else permissionLauncher.launch(arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ))
//    }) { Text("Take photo") }
}


