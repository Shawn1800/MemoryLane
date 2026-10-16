package com.ghostbug.memorylane.features.profile.presentation

import androidx.compose.material3.DatePickerState
import kotlinx.datetime.LocalDate


data class ProfileState(
    val loading: Boolean = false,
    val name : String = "",
    val username : String = "",
    val dob : LocalDate?=null,
    val error : String=""
)