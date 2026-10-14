package com.ghostbug.memorylane.features.profile.domain.repository

import com.ghostbug.memorylane.features.profile.data.repositoryImpl.ProfileRepositoryImpl

interface ProfileRepository
{

    suspend fun getname():String
    suspend fun getUsername(): String
    suspend fun getBio(): String
    suspend fun getBirthDate(): String
    suspend fun getEmail(): String

    suspend fun putName(): String
    suspend fun putUsername(): String
    suspend fun putBio(): String
    suspend fun putBirthDate(): String

}


