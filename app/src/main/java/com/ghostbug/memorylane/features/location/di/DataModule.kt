package com.ghostbug.memorylane.features.location.di

import com.ghostbug.memorylane.core.data.datasouce.LocationDataSource
import com.ghostbug.memorylane.features.location.data.repositoryImpl.LocationRepositoryImpl
import com.ghostbug.memorylane.features.location.domain.repository.LocationRepository
import org.koin.dsl.module

val dataModule = module {
    // androidContext() gives Koin the app Context, which LocationDataSource needs.
    single { LocationDataSource() }

    // Bind the interface to its implementation. get() resolves LocationDataSource above.
    single<LocationRepository> { LocationRepositoryImpl(get()) }
}