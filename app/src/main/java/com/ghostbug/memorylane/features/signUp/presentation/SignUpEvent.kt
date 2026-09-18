package com.ghostbug.memorylane.features.signUp.presentation

sealed class SignUpEvent {
    data class OnEmailChanged(val email:String ) :SignUpEvent()
    data class OnPasswordChanged(val password :String) : SignUpEvent()

    object OnForgotPasswordClick : SignUpEvent()
    data object onSignUpClick : SignUpEvent()

    data object  OnGoogleSignInClick : SignUpEvent()


}