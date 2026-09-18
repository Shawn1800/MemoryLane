package com.ghostbug.memorylane.features.location.di

import com.ghostbug.memorylane.features.location.domain.LocationUseCase
import org.koin.dsl.module

val locationDomainModule = module {
    // get() resolves LocationRepository, which dataModule provides.
    factory { LocationUseCase(get()) }
}