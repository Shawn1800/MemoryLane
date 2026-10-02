package com.ghostbug.memorylane.features.camera.presentation

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun CameraCapture() {
    var  imageBitmap by  remember { mutableStateOf<Bitmap?>(null)}

    val  cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {pictureTaken->
        if (pictureTaken) {

        }
    }
}



