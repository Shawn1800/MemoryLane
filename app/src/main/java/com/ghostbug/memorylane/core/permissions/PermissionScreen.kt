package com.ghostbug.memorylane.core.permissions

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PermissionScreen(
    permissionsViewModel: PermissionsViewModel= koinViewModel(),
    ) {

    val dialogQueue = permissionsViewModel.visiblePermissionDialogQueue
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {isGranted->
            permissionsViewModel.onPermissionResult(
                permission = Manifest.permission.CAMERA,
                   granted = isGranted
            )
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            ) }) {
            Text(text = "Request one permission")
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}