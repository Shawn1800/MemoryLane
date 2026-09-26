package com.ghostbug.memorylane.features.profile.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class Profile(
    val userId: String,
    val email: String,
    val name: String = "",
    val userName: String = "",
    val bio: String? = null,
    val invitedBy: String? = null,
    val inviteCode: String = "",
    val inviteCount: Int = 0,
    val accountCreatedAt: Instant? = null,
    val birthDate: LocalDate? = null,
    val phoneNumber: String? = null,
    val profilePic: String? = null,
)
