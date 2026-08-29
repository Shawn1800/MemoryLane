plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:<version>")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")

}