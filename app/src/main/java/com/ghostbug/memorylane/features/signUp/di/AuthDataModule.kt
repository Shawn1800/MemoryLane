package com.ghostbug.memorylane.features.signUp.di

import com.ghostbug.memorylane.BuildConfig
import com.ghostbug.memorylane.features.signUp.auth.data.repositoryImpl.AuthRepositoryImpl
import com.ghostbug.memorylane.features.signUp.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import org.koin.dsl.module

val authDataModule = module {

    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        ) {
            install(Postgrest)
            install(Auth)
        }
    }
    single { get<SupabaseClient>().auth }
    single { get<SupabaseClient>().postgrest }
    single<AuthRepository> { AuthRepositoryImpl(get(),get()) }
}