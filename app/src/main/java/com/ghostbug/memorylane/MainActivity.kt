package com.ghostbug.memorylane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ghostbug.memorylane.core.navigation.MainNavigation
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainNavigation()
            }
        }
    }


//val supabase = createSupabaseClient(
//    supabaseUrl = BuildConfig.SUPABASE_URL,
//    supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
//) {
//    install(Auth)
//    install(Postgrest)
//
//}

