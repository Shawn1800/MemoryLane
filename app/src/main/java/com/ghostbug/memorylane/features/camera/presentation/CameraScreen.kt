package com.ghostbug.memorylane.features.camera.presentation


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
import com.ghostbug.memorylane.features.camera.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.location.presentation.LocationViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import kotlin.time.Clock


@Composable
fun CameraScreen(
    onCaptured:(CapturedMemory)->Unit,
    locationViewModel: LocationViewModel = koinViewModel(),
    permissionsViewModel: PermissionsViewModel= koinViewModel(),
    cameraViewModel: CameraViewModel = koinViewModel(),
    modifier: Modifier
//    onBack: Unit
) {
    val cameraState by cameraViewModel.state.collectAsStateWithLifecycle()
    val locationState by locationViewModel.state.collectAsStateWithLifecycle()
    var pendingUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var capturedUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var capturedMemory by rememberSaveable { mutableStateOf<CapturedMemory?>(null) }
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
                caption = "will add caption later"
            )
            capturedMemory = memory
            onCaptured(memory)
        }
    }
    fun launchCamera() {
        val file = File.createTempFile("memory_",".jpeg", context.cacheDir)
        val uri  = FileProvider.getUriForFile(context,"${context.packageName}.fileProvider",file)
        pendingUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(Unit) {
        launchCamera()
    }

    if (capturedUri!=null) {
        DisplayImageAfterClick(
            onBack= { /*TODO*/ },
            imageUri = capturedUri,
            modifier = modifier,
            onUpload = {
                capturedMemory?.let { memory ->
                    cameraViewModel.onEvent(CameraEvent.onUploadClicked(memory)) }
                }

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


