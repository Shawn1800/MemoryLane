package com.ghostbug.memorylane.features.profile.data.repositoryImpl

import androidx.compose.foundation.layout.Column
import com.ghostbug.memorylane.features.profile.data.remote.ProfileDto
import com.ghostbug.memorylane.features.profile.domain.repository.ProfileRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.utils.io.CancellationException

class ProfileRepositoryImpl (
    private val postgrest: Postgrest
) : ProfileRepository{
    override suspend fun getname(): String {
        TODO("Not yet implemented")
    }
    override suspend fun getUsername(): String {
        TODO("Not yet implemented")
    }
    override suspend fun getBio(): String {
        TODO("Not yet implemented")
    }
    override suspend fun getBirthDate(): String {
        TODO("Not yet implemented")
    }
    override suspend fun getEmail(): String {
        TODO("Not yet implemented")
    }
    override suspend fun putName(): String {
        return  try {
           val name =  postgrest.from("profiles").insert(ProfileDto)
          name.toString()
        }  catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
    override suspend fun putUsername(): String {
        TODO("Not yet implemented")
    }
    override suspend fun putBio(): String {
        TODO("Not yet implemented")
    }
    override suspend fun putBirthDate(): String {
        TODO("Not yet implemented")
    }

}