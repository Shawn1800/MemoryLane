package com.ghostbug.memorylane.core.auth.domain.repository

import androidx.browser.trusted.Token

interface  AuthRepository {
    suspend fun logIn (email:String,password:String) : Result<Unit>
    suspend fun signUp(email:String,password: String) : Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun signInWithGoogle(): Result<Unit>

    suspend fun isEmailInPublicUsersTable(email: String): Result<Unit>

    suspend fun verifyEmail(email: String,token: String): Result<Unit>

    suspend fun signUpWithOtp(email: String):Result<Unit>


}

