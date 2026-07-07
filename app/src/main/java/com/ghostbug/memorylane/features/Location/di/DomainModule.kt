package com.ghostbug.memorylane.features.Location.di

import com.ghostbug.memorylane.features.Location.domain.LocationUseCase
import org.koin.dsl.module

val domainModule = module {
    // get() resolves LocationRepository, which dataModule provides.
    factory { LocationUseCase(get()) }
}