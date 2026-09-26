package com.ghostbug.memorylane.features.signUp.presentation

sealed class SignUpUiEvent {
    data object onSignUp : SignUpUiEvent()
}