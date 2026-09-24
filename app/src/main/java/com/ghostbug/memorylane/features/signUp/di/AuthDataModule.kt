package com.ghostbug.memorylane.features.signUp.di

import com.ghostbug.memorylane.features.signUp.auth.data.repositoryImpl.AuthRepositoryImpl
import com.ghostbug.memorylane.features.signUp.auth.domain.repository.AuthRepository
import io.github.jan.supabase.auth.Auth
import org.koin.dsl.module

val authDataModule = module {
    single { Auth }
    single < AuthRepository>{ AuthRepositoryImpl(get()) }
}