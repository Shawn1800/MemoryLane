import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val properties = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.inputStream().use { properties.load(it) }
}
android {
    namespace = "com.ghostbug.memorylane"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }

    }
    defaultConfig {
        applicationId = "com.ghostbug.memorylane"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val supabaseUrl = properties.getProperty("SUPABASE_URL") ?: ""
        val supabaseKey = properties.getProperty("SUPABASE_PUBLISHABLE_KEY") ?: ""
        val googleClientId = properties.getProperty("GOOGLE_CLIENT_ID") ?: ""
        val webClientId = properties.getProperty("WEB_CLIENT_ID") ?: ""

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"$supabaseKey\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"$googleClientId\"")
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true

    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation("com.mapbox.maps:android-ndk27:11.25.0")
    implementation("com.mapbox.extension:maps-compose-ndk27:11.25.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)  // No version need
    implementation(libs.koin.compose)            // KoinContext + koinInject() for Compose
    implementation(libs.koin.compose.viewmodel)  // koinViewModel() for Compose

    implementation("androidx.navigation3:navigation3-runtime:1.2.0-alpha07")
    implementation("androidx.navigation3:navigation3-ui:1.2.0-alpha07")
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3")

    implementation("androidx.compose.material:material-icons-extended:<version>")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")

    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.8.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:3.8.0")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.8.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:3.8.0")


    implementation("io.ktor:ktor-client-android:3.5.2")
    implementation("io.ktor:ktor-client-core:3.5.2")
    implementation("io.ktor:ktor-utils:3.5.2")

    implementation("androidx.credentials:credentials:1.7.0-alpha03")
    implementation("androidx.credentials:credentials-play-services-auth:1.7.0-alpha03")
    implementation("com.google.android.libraries.identity.googleid:googleid:<latest version>")

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.7.0-alpha03"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${camerax_version}")
}