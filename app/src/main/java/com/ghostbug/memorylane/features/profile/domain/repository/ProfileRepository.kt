package com.ghostbug.memorylane.features.profile.domain.repository

import com.ghostbug.memorylane.features.profile.data.repositoryImpl.ProfileRepositoryImpl
import com.ghostbug.memorylane.features.profile.domain.model.Profile

interface ProfileRepository
{
    suspend fun insertProfile(profile: Profile) :Result<Unit>
    suspend fun updateProfile(profile: Profile) :Result<Unit>
    suspend fun getProfile() :Result<Profile>


}


