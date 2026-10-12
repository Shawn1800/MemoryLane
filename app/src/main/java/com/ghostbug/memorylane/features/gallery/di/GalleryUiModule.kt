package com.ghostbug.memorylane.features.gallery.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostbug.memorylane.features.gallery.presentation.GalleryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val GalleryUiModule = module {
    viewModel {
        GalleryViewModel(get())
    }
}