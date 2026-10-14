package com.ghostbug.memorylane.features.capture.di

import com.ghostbug.memorylane.features.capture.data.repositoryImpl.CameraManagerRepositoryImpl
import com.ghostbug.memorylane.features.capture.domain.repository.CameraManagerRepository
import org.koin.dsl.module

val   CameraDomainModule = module {
    single<CameraManagerRepository> {
        CameraManagerRepositoryImpl(get(), get(), get(),get(),get())
    }
}