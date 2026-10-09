package com.ghostbug.memorylane.core.permissions

import android.Manifest
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghostbug.memorylane.core.permissions.PermissionManager.Companion.REQUIRED_PERMISSIONS_POST_T
import com.ghostbug.memorylane.core.permissions.PermissionManager.Companion.REQUIRED_PERMISSIONS_PRE_T
import kotlinx.coroutines.flow.SharedFlow
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    permissionsViewModel: PermissionsViewModel= koinViewModel(),
    onNavigate: () -> Unit,
    uiEvent : SharedFlow<PermissionUiEvent>
    ) {

    val appContext  = LocalContext.current
    val activityContext  = LocalActivity.current
    val state by permissionsViewModel.permissionState.collectAsStateWithLifecycle()
    var hasRequestedPermission by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        uiEvent.collect{
            event ->
            when (event)  {
                is PermissionUiEvent.onPermissionGranted -> {
                   onNavigate()
                }
            }
        }
    }

    val requestPermission = rememberLauncherForActivityResult( RequestMultiplePermissions()){ permissions ->
        hasRequestedPermission = true
        permissionsViewModel.onPermissionChange(permissions)
    }

    LaunchedEffect(Unit) {
        permissionsViewModel.checkPermissions()
    }

    fun openSetting(){
        appContext.startActivity(permissionsViewModel.openSettings())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permissions") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "You have to grant access to these permissions in order to use the app"
            )
            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Camera access") },
                supportingContent = { Text("Take picture when clicking  a picture and saving it on map") },
                trailingContent = { state.hasCameraAccess },
                leadingContent = {
                    Icon(
                        Icons.Filled.Camera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Precise location access") },
                supportingContent = { Text("Keep track of the location of the picture") },
                trailingContent = { PermissionAccessIcon(state.hasLocationAccess) },
                leadingContent = {
                    Icon(
                        Icons.Filled.Explore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            )

            Spacer(Modifier.height(32.dp))

            if (state.hasAllAccess()) {
                FilledTonalButton(onClick = { onNavigate() }) {
                    Text("Get started")
                }
            } else {
                if (hasRequestedPermission) {
                    FilledTonalButton(onClick = { openSetting() }) {
                        Text("Go to settings")
                    }
                } else {
                    FilledTonalButton(onClick = {
                        if (Build.VERSION.SDK_INT >= 33) {
                            if (activityContext != null) {
                              requestPermission.launch(REQUIRED_PERMISSIONS_POST_T,)
                            }
                        }
                        else {
                            if (activityContext != null) {
                                requestPermission.launch(REQUIRED_PERMISSIONS_PRE_T,)
                            }
                        }
                    }) {
                        Text("Request permissions")
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionAccessIcon(hasAccess: Boolean) {
    if (hasAccess) {
        Icon(
            Icons.Filled.Check,
            contentDescription = "Permission accepted"
        )
    } else {
        Icon(
            Icons.Filled.Close,
            contentDescription = "Permission not granted"
        )
    }
}