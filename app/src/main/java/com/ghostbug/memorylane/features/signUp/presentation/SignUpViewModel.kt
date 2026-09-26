package com.ghostbug.memorylane.features.signUp.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbug.memorylane.features.signUp.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SignUpViewModel(
    private val authRepository: AuthRepository,
): ViewModel() {

    private val _signUpState = MutableStateFlow(SignUpState())
    var signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    private val _signUpUiEvent = MutableSharedFlow<SignUpUiEvent>()
    var signUpUiEvent: SharedFlow<SignUpUiEvent> = _signUpUiEvent.asSharedFlow()



    fun onEvent(event: SignUpEvent) { when (event) {
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

            SignUpEvent.OnForgotPasswordClick -> {
                TODO()
            }

        is SignUpEvent.OnGoogleSignInResult -> {
            viewModelScope.launch {
                _signUpState.update {
                    it.copy(isLoading = true, error = null)
                }
                authRepository.signInWithGoogle(event.idToken, event.rawNonce)
                    .onSuccess {
                        _signUpState.update {
                            it.copy(
                                isLoading = false,
                                isSignUpSuccessful = true
                            )
                        }
                        _signUpUiEvent.emit(SignUpUiEvent.onSignUp)

                    }.onFailure { e ->
                        _signUpState.update {
                            it.copy(
                                isLoading = false,
                                error = e.message
                            )
                        }
                    }
            }

        }
    }
    }
}
