package com.ghostbug.memorylane.features.location.di

import com.ghostbug.memorylane.features.location.data.repositoryImpl.LocationRepositoryImpl
import com.ghostbug.memorylane.features.location.domain.LocationUseCase
import com.ghostbug.memorylane.features.location.domain.repository.LocationRepository
import org.koin.dsl.module

val locationDomainModule = module {
    // get() resolves LocationRepository, which dataModule provides.
    factory { LocationUseCase(get()) }
    // Bind the interface to its implementation. get() resolves LocationDataSource above.
    single<LocationRepository> { LocationRepositoryImpl(get()) }
}