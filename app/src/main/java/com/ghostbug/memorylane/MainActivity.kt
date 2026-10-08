package com.ghostbug.memorylane

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ghostbug.memorylane.core.navigation.MainNavigation
import com.mapbox.common.MapboxOptions
import com.mapbox.search.autocomplete.PlaceAutocomplete
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)
        val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                // get suggestions for the search query "Washington DC"
                val response = placeAutocomplete.suggestions(query = "Washington DC")
                if (response.isValue) {
                    val suggestions = response.value.orEmpty()
                    Log.i("SearchExample", "Suggestions: $suggestions")

                    if (suggestions.isNotEmpty()) {
                        val result = placeAutocomplete.select(suggestions.first())
                        result.onValue { Log.i("SearchExample", "Result: $it") }
                        result.onError { Log.e("SearchExample", "Error selecting suggestion", it) }
                    }
                } else {
                    Log.e("SearchExample", "Error fetching suggestions: ${response.error}")
                }
            }
        }
        setContent {
            MainNavigation()
        }
    }
}