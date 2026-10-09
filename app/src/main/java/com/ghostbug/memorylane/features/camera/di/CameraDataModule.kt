package com.ghostbug.memorylane.features.camera.di


import com.ghostbug.memorylane.features.camera.data.repositoryImpl.CameraManagerRepositoryImpl
import com.ghostbug.memorylane.features.camera.data.repositoryImpl.utils.ReverseGeocoder
import org.koin.dsl.module

val  CameraDataModule = module {
    single {
        ReverseGeocoder()
    }
    single {
        CameraManagerRepositoryImpl(get(), get(), get(),get(),get())
    }
}