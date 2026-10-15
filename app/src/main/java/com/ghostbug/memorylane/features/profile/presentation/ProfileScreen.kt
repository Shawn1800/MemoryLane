package com.ghostbug.memorylane.features.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ghostbug.memorylane.features.signUp.presentation.SignUpEvent
import com.ghostbug.memorylane.features.signUp.presentation.SignUpViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ProfileScreen(
    signUpViewModel: SignUpViewModel = koinViewModel (),
    onSignOut: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = {
                signUpViewModel.onEvent(SignUpEvent.SignOut)
                onSignOut()
            }
        ) {
            Text("Sign Out")
        }
    }

}