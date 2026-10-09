package com.ghostbug.memorylane.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object LocationScreen : Route
    @Serializable
    data object SignUpScreen :Route

    @Serializable
    data object CameraScreen :Route
}