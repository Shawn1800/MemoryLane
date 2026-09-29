package com.ghostbug.memorylane.features.camera.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.imageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import org.koin.compose.viewmodel.koinViewModel


//@Composable
//fun CameraRoute(
//    cameraViewModel: CameraViewModel= koinViewModel ()
//
//) {
//    val dialogQueue = cameraViewModel.visiblePermissionDialogQueue
//    CameraScreen()
//}
//
//
//@Composable
//fun CameraScreen( ) {
//
//
//}


