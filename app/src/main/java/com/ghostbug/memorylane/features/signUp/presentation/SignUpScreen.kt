package com.ghostbug.memorylane.features.signUp.presentation

import android.content.MutableContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostbug.memorylane.BuildConfig.WEB_CLIENT_ID
import com.ghostbug.memorylane.features.signUp.auth.data.repositoryImpl.generateSecureRandomNonce
import com.ghostbug.memorylane.features.signUp.auth.utils.GoogleSignUpUtils
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.onSuccess

@Composable
fun SignUpRoute(
    onSignUp: () -> Unit ,
    modifier: Modifier,
    signUpViewModel: SignUpViewModel = koinViewModel()
) {
    val state by signUpViewModel.signUpState.collectAsStateWithLifecycle()
    SignUpScreen(state = state,
        onEvent = signUpViewModel::onEvent,
        modifier = modifier
    )
}
@Composable
fun SignUpScreen(
    state : SignUpState,
    onEvent : (SignUpEvent) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    GoogleSignInButton(
        isLoading = state.isLoading,
        onTokenReceived = {token , nonce ->
            onEvent(SignUpEvent.OnGoogleSignInResult(token , nonce))
        },
        onError  = { msg ->
            scope.launch { snackbarHostState }
        }
    )
}
@Composable
fun GoogleSignInButton(onClick : () -> Unit) {

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Button(
            onClick = onClick,
        ) {
            Text(text = "Sign in with Google")
        }
    }
}

{
    val credentialManager = CredentialManager.create(context)
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(WEB_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .setNonce(generateSecureRandomNonce())
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val mutableContext = MutableContextWrapper(context)
    scope.launch {
        try {
            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                onTokenReceived(googleIdTokenCredential.idToken, rawNonce)
            }
        } catch (e: GetCredentialException) {
            onError(e.message ?: "Sign-in didn't go through. Try again?")
        } catch (e: Exception) {
            onError(e.message ?: "Something went wrong. Try again?")
        }
    }

}