package com.ghostbug.memorylane.features.capture.di


import com.ghostbug.memorylane.features.capture.data.repositoryImpl.CameraManagerRepositoryImpl
import com.ghostbug.memorylane.features.capture.data.repositoryImpl.utils.ReverseGeocoder
import org.koin.dsl.module

val  CameraDataModule = module {
    single {
        ReverseGeocoder()
    }
    single {
        CameraManagerRepositoryImpl(get(), get(), get(),get(),get())
    }
}