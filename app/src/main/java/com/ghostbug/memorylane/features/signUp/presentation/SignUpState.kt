package com.ghostbug.memorylane.features.signUp.presentation

data class SignUpState (
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error : String? = "" ,
    val isSignUpSuccessful : Boolean = false
)
