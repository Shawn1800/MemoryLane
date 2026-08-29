package com.ghostbug.memorylane.features.profile.domain.model

import kotlinx.datetime.LocalDate

data class Profile (
    val id : String ,
    val name :String,
    val userName : String,
    val birthDate : LocalDate?,
    val email : String,
    val phoneNumber : String?
)
