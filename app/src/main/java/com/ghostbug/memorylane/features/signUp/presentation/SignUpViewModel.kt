package com.ghostbug.memorylane.features.signUp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostbug.memorylane.core.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository): ViewModel() {
    private val _signUpState = MutableStateFlow(SignUpState())
    var signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnEmailChanged ->{

                _signUpState.update {
                    it.copy(email = event.email)
                }
            }

            is SignUpEvent.OnPasswordChanged -> {
                _signUpState.update {
                    it.copy(password = event.password)
                }
            }

            is SignUpEvent.onSignUpClick -> {
                viewModelScope.launch {
                    _signUpState.update {
                        it.copy(isLoading = true)
                    }
                    authRepository.signUp(_signUpState.value.email, _signUpState.value.password)
                }
            }


            is SignUpEvent.OnGoogleSignInClick -> {
                viewModelScope.launch {
                    authRepository.signInWithGoogle()
                }
            }

            SignUpEvent.OnForgotPasswordClick -> TODO()
        }
    }




}