package com.ghostbug.memorylane.features.camera.presentation

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ghostbug.memorylane.core.permissions.PermissionScreen
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
import com.ghostbug.memorylane.features.camera.data.cache.CapturedMemory
import com.ghostbug.memorylane.features.location.presentation.LocationViewModel
import io.github.jan.supabase.storage.resumable.createDefaultResumableCache
import java.io.File
import kotlin.time.Clock
import kotlin.time.Instant


@Composable
fun CameraScreen(
    onCaptured:(CapturedMemory)->Unit,
    locationViewModel: LocationViewModel,
    permissionsViewModel: PermissionsViewModel,
//    onBack: Unit
) {

    val locationState by locationViewModel.state.collectAsStateWithLifecycle()
    var pendingUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val  cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { pictureTaken->
        val uri = pendingUri
        if (pictureTaken && uri!=null) {
            onCaptured(CapturedMemory(
                uri=uri,
                latitude = locationState.latitude,
                longitude = locationState.longitude,
                timeStamp = Clock.System.now()
            ))
        }
    }

    fun launchCamera() {
        val file = File.createTempFile("memory_",".jpeg", context.cacheDir)
        val uri  = FileProvider.getUriForFile(context,"${context.packageName}.fileProvider",file)
        pendingUri = uri
        cameraLauncher.launch(uri)
    }



    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { launchCamera() } // still take the photo if denied; lat/lng will be null

    LaunchedEffect(Unit) {
        launchCamera()
    }

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

@Composable
fun DisplayImageAfterClick(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null
) {
    if (imageUri != null) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Captured image",
                modifier = Modifier.size(250.dp)
            )
        }
    }
}
