package com.ghostbug.memorylane.features.profile.data.repositoryImpl

import androidx.compose.foundation.layout.Column
import com.ghostbug.memorylane.features.profile.data.remote.ProfileDto
import com.ghostbug.memorylane.features.profile.data.remote.ProfileInsertDto
import com.ghostbug.memorylane.features.profile.data.remote.toDomain
import com.ghostbug.memorylane.features.profile.domain.model.Profile
import com.ghostbug.memorylane.features.profile.domain.repository.ProfileRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class ProfileRepositoryImpl (
    private val postgrest: Postgrest,
    private val auth : Auth
) : ProfileRepository{
    private companion object {
        const val PROFILES = "profiles"
        const val USERID = "user_id"
    }



    override  suspend fun insertProfile(profile: Profile) :Result<Unit>  {
        return try  {
            val user = auth.currentSessionOrNull()?.user ?: return Result.failure(Exception("User not logged in"))
            val  avatarUrl = user?.userMetadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull
            postgrest.from(PROFILES).insert(ProfileInsertDto(
                name = profile.name,
                userName = profile.userName.trim().lowercase(),
                bio = profile.bio,
                birthDate = profile.birthDate,
                profilePic = profile.profilePic?:avatarUrl,
            )
            ) {
                filter {
                    eq(USERID,user.id)
                }
            }
                Result.success(Unit)
            }  catch (e: CancellationException)   {
                throw e
            }   catch (e: Exception)   {
                Result.failure(e)
            }
        }

    override  suspend fun updateProfile(profile: Profile) :Result<Unit>  {
        return try  {
            val user = auth.currentSessionOrNull()?.user ?: return Result.failure(Exception("User not logged in"))
            val  avatarUrl = user?.userMetadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull
            postgrest.from(PROFILES).update(ProfileInsertDto(
                name = profile.name,
                userName = profile.userName.trim().lowercase(),
                bio = profile.bio,
                birthDate = profile.birthDate,
                profilePic = profile.profilePic?:avatarUrl,
            )
            ) {
                filter {
                    eq(USERID,user.id)
                }
            }
            Result.success(Unit)
        }  catch (e: CancellationException)   {
            throw e
        }   catch (e: Exception)   {
            Result.failure(e)
        }
    }



    override suspend fun getProfile(): Result<Profile> {
        return try {
            val user = auth.currentSessionOrNull()?.user
                ?: return Result.failure(Exception("User not logged in"))
            val profile =   postgrest.from(PROFILES).select {
                filter {
                    eq(USERID, user.id)
                }
            }
                .decodeSingle<ProfileDto>()
                .toDomain()
            Result.success(profile)
        }
        catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    }

