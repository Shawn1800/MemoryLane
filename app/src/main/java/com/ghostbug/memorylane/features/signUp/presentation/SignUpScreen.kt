package com.ghostbug.memorylane.features.signUp.presentation

import android.content.MutableContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostbug.memorylane.BuildConfig.WEB_CLIENT_ID

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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoogleSignInButton(
            isLoading = state.isLoading,
            onTokenReceived = { token, nonce ->
                onEvent(SignUpEvent.OnGoogleSignInResult(token, nonce))
            },
            onError = { msg ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = msg
                    ) }
            }
        )
    }
}}
@Composable
fun GoogleSignInButton(
    isLoading: Boolean,
    onTokenReceived: (String, String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Button(
            onClick = {
                val rawNonce = java.util.UUID.randomUUID().toString()
                val hashedNonce = hashNonce(rawNonce)
                val credentialManager = CredentialManager.create(context)

                fun buildRequest(filterByAuthorized: Boolean) = GetCredentialRequest.Builder()
                    .addCredentialOption(
                        GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(filterByAuthorized)
                            .setServerClientId(WEB_CLIENT_ID)
                            .setAutoSelectEnabled(filterByAuthorized) // only makes sense when filtering
                            .setNonce(hashedNonce)
                            .build()
                    )
                    .build()

                scope.launch {
                    try {
                        val result = try {
                            credentialManager.getCredential(context = context, request = buildRequest(true))
                        } catch (e: NoCredentialException) {
                            credentialManager.getCredential(context = context, request = buildRequest(false))
                        }
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
        ) {
            Text(text = "Sign in with Google")
        }
    }
}
private fun hashNonce(nonce: String): String {
    val md = java.security.MessageDigest.getInstance("SHA-256")
    val digest = md.digest(nonce.toByteArray())
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}


