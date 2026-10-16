package com.ghostbug.memorylane.features.profile.di

import com.ghostbug.memorylane.features.profile.data.repositoryImpl.ProfileRepositoryImpl
import com.ghostbug.memorylane.features.profile.domain.repository.ProfileRepository
import org.koin.dsl.module

val ProfileDataModule=  module{
    single {
        ProfileRepositoryImpl(get(),get())
    }
}