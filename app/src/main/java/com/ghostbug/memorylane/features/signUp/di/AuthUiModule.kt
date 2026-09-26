package com.ghostbug.memorylane.features.signUp.di

import com.ghostbug.memorylane.features.signUp.presentation.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val authUiModule = module {
    viewModel{ SignUpViewModel(get()) }
}