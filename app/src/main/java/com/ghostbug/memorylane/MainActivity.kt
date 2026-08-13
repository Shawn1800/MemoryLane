package com.ghostbug.memorylane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ghostbug.memorylane.core.navigation.MainNavigation
import com.ghostbug.memorylane.features.Location.domain.LocationUseCase
import com.ghostbug.memorylane.features.Location.presentation.LocationViewModel
import com.ghostbug.memorylane.features.Location.presentation.MapScreen

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainNavigation()
            }
        }
    }




