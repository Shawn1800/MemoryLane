package com.ghostbug.memorylane.features.capture.di

import com.ghostbug.memorylane.features.capture.presentation.CameraViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val  CameraUiModule = module{
    viewModel {
        CameraViewModel(get())
    }
}
