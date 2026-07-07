package com.ghostbug.memorylane.features.Location.di

import com.ghostbug.memorylane.core.data.datasouce.LocationDataSource
import com.ghostbug.memorylane.features.Location.data.repositoryImpl.LocationRepositoryImpl
import com.ghostbug.memorylane.features.Location.domain.repository.LocationRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    // androidContext() gives Koin the app Context, which LocationDataSource needs.
    single { LocationDataSource(androidContext()) }

    // Bind the interface to its implementation. get() resolves LocationDataSource above.
    single<LocationRepository> { LocationRepositoryImpl(get()) }
}