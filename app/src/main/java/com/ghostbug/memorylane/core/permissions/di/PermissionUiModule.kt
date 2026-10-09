package com.ghostbug.memorylane.core.permissions.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostbug.memorylane.core.permissions.PermissionsViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
val  permissionUiModule = module {
    viewModel{ PermissionsViewModel(get()) }
}