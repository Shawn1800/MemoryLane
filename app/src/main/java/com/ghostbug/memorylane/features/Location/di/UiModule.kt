package com.ghostbug.memorylane.features.Location.di

import com.ghostbug.memorylane.features.Location.presentation.LocationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    // viewModel {} ties the instance to the Android ViewModel lifecycle.
    // get() resolves LocationUseCase from domainModule.
    viewModel { LocationViewModel(get()) }
}