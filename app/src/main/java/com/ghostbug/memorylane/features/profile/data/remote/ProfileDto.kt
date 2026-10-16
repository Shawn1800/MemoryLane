package com.ghostbug.memorylane.features.profile.data.remote

import com.ghostbug.memorylane.features.profile.domain.model.Profile
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ProfileDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String = "",
    @SerialName("username")
    val userName: String = "",
    @SerialName("bio")
    val bio: String? ,
    @SerialName("invited_by")
    val invitedBy: String?=null,
    @SerialName("invite_code")
    val inviteCode: Int?=0,
    @SerialName("invite_count")
    val inviteCount: Int? = 0,
    @SerialName("acc_created_at")
    val accountCreatedAt: Instant? ,
    @SerialName("birth_date")
    val birthDate: LocalDate? ,
    @SerialName("phone_no")
    val phoneNumber: String? ,
    @SerialName("profile_pic")
    val profilePic: String? ,
)
fun ProfileDto.toDomain(): Profile {
    return Profile (
        userId = userId,
        name = name,
        userName = userName,
        bio = bio,
        invitedBy = invitedBy,
        inviteCode = inviteCode,
        inviteCount = inviteCount,
        accountCreatedAt = accountCreatedAt,
        birthDate = birthDate,
        phoneNumber = phoneNumber,
        profilePic = profilePic
    )
}

@Serializable
data class ProfileInsertDto(
    @SerialName("name")
    val name: String = "",
    @SerialName("username")
    val userName: String = "",
    @SerialName("bio")
    val bio: String? ,
    @SerialName("birth_date")
    val birthDate: LocalDate? , // Correctly serializes to "birth_date"
    @SerialName("profile_pic")
    val profilePic: String? ,
)