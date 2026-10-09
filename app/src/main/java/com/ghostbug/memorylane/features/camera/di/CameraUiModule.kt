package com.ghostbug.memorylane.features.camera.di

import com.ghostbug.memorylane.features.camera.presentation.CameraViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val  CameraUiModule = module{
    viewModel {
        CameraViewModel(get())
    }
}
