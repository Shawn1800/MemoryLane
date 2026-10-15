package com.ghostbug.memorylane.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbug.memorylane.features.profile.domain.model.Profile
import com.ghostbug.memorylane.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel (
    val repository: ProfileRepository
): ViewModel() {
    val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.onNameChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            name = event.name
                        )
                    }
                }
            }

            is ProfileEvent.onDobChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            dob = event.dob
                        )
                    }
                }
            }

            is ProfileEvent.onUsernameChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            username = event.username
                        )
                    }
                }
            }

            is ProfileEvent.onSave -> {
                val current = _state.value
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                    repository.insertProfile(
                        Profile(
                            name = current.name,
                            userName = current.username,
                            birthDate = current.dob
                        )
                    )
                        .onSuccess {
                            _uiEvent.emit(ProfileUiEvent.onNavHomeSceen)
                        }.onFailure { e ->
                            _state.update {
                                it.copy(
                                    error = e.message ?: "Unknown Error"
                                )
                            }
                        }
                }
            }
        }
    }
}