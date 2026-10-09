package com.ghostbug.memorylane.core.permissions.di

import com.ghostbug.memorylane.core.permissions.PermissionManager
import org.koin.dsl.module

val  permissionDataModule = module  {
    single{
        PermissionManager(get())
    }
}