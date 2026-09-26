package com.ghostbug.memorylane

import android.app.Application
import com.ghostbug.memorylane.features.location.di.locationDataModule
import com.ghostbug.memorylane.features.location.di.locationDomainModule
import com.ghostbug.memorylane.features.location.di.locationUiModule
import com.ghostbug.memorylane.features.signUp.di.authDataModule
import com.ghostbug.memorylane.features.signUp.di.authUiModule

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            // Without this line, Koin knows nothing — every inject/get would crash.
            modules(locationDataModule, locationDomainModule, locationUiModule)
            modules(authDataModule, authUiModule)
        }
    }
}