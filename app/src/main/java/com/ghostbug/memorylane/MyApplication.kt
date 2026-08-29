package com.ghostbug.memorylane

import android.app.Application
import com.ghostbug.memorylane.features.location.di.dataModule
import com.ghostbug.memorylane.features.location.di.domainModule
import com.ghostbug.memorylane.features.location.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            // Without this line, Koin knows nothing — every inject/get would crash.
            modules(dataModule, domainModule, uiModule)
        }
    }
}