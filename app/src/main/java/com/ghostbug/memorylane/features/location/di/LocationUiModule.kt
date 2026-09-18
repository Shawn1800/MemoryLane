package com.ghostbug.memorylane.features.location.di

import com.ghostbug.memorylane.features.location.presentation.LocationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val locationUiModule = module {
    // viewModel {} ties the instance to the Android ViewModel lifecycle.
    // get() resolves LocationUseCase from domainModule.
    viewModel { LocationViewModel(get()) }
}