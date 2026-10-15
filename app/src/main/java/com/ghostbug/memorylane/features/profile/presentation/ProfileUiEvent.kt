package com.ghostbug.memorylane.features.profile.presentation

sealed class ProfileUiEvent {
    data object onNext: ProfileUiEvent()
    data object onBack: ProfileUiEvent()
    data object onNavHomeSceen: ProfileUiEvent()
}