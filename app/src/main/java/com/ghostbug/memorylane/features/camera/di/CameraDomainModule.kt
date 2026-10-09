package com.ghostbug.memorylane.features.camera.di

import com.ghostbug.memorylane.features.camera.data.repositoryImpl.CameraManagerRepositoryImpl
import com.ghostbug.memorylane.features.camera.domain.cache.CapturedMemory
import com.ghostbug.memorylane.features.camera.domain.repository.CameraManagerRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val   CameraDomainModule = module {
    single<CameraManagerRepository> {
        CameraManagerRepositoryImpl(get(), get(), get(),get(),get())
    }
}