package com.ghostbug.memorylane.features.profile.data.remote

import com.ghostbug.memorylane.features.profile.domain.model.Profile
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
class ProfileDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String = "",
    @SerialName("username")
    val userName: String = "",
    @SerialName("bio")
    val bio: String? = null,
    @SerialName("invited_by")
    val invitedBy: String? = null,
    @SerialName("invite_code")
    val inviteCode: String = "",
    @SerialName("invite_count")
    val inviteCount: Int = 0,
    @SerialName("acc_created_at")
    val accountCreatedAt: Instant? = null,
    @SerialName("birth_date")
    val birthDate: LocalDate? = null,
    @SerialName("phone_no")
    val phoneNumber: String? = null,
    @SerialName("profile_pic")
    val profilePic: String? = null,
)
fun ProfileDto.toDomain(): Profile {
    return Profile (
        userId = userId,
        email = email,
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