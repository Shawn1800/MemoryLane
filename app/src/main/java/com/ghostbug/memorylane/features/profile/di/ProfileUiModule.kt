package com.ghostbug.memorylane.features.profile.di

import com.ghostbug.memorylane.features.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val ProfileUiModule= module {
    viewModel {
        ProfileViewModel(get())
    }

}