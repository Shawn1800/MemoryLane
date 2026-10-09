package com.ghostbug.memorylane.core.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(val icon : ImageVector)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.LocationScreen    to BottomNavItem(icon = Icons.Default.Home),
    Route.CameraScreen to BottomNavItem(icon = Icons.Default.Camera)
)