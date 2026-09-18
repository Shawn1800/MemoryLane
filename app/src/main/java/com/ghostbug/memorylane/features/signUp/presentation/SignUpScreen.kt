package com.ghostbug.memorylane.features.signUp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state by viewModel.signUpState.collectAsStateWithLifecycle()
    SignUpContent()
}

@Composable
fun SignUpContent(){

}