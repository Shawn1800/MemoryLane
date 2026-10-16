package com.ghostbug.memorylane.features.profile.presentation

import android.widget.DatePicker
import androidx.compose.material3.DatePickerState
import com.ghostbug.memorylane.features.profile.domain.model.Profile
import kotlinx.datetime.LocalDate

sealed class ProfileEvent {
    data class onNameChanged(val name: String): ProfileEvent()
    data class onUsernameChanged(val username: String): ProfileEvent()
    data class onDobChanged(val dob: LocalDate): ProfileEvent()
    data object onSave: ProfileEvent()
}