package com.ghostbug.memorylane.features.signUp.auth.data.repositoryImpl

import android.content.Context
import android.content.MutableContextWrapper
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.ghostbug.memorylane.BuildConfig
import com.ghostbug.memorylane.features.signUp.auth.domain.repository.AuthRepository
import com.ghostbug.memorylane.features.signUp.auth.utils.GoogleSignUpUtils
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlin.coroutines.cancellation.CancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.coroutineScope
import java.security.SecureRandom


class AuthRepositoryImpl(
    private val auth: Auth,
    private val postgrest: Postgrest,
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override  suspend fun signUpWithOtp(email:String):Result<Unit> {
        return try {
            auth.signInWith(OTP) {
                this.email = email
            }
            Result.success(Unit)
        } catch (e : CancellationException) {
            throw e
        } catch(e:Exception) {
            Result.failure(e)
        }
    }



    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String, nonce:String): Result<Unit> {
          return try {
                 auth.signInWith(IDToken) {
                     this.idToken = idToken
                     provider = Google
                     this.nonce = nonce
                 }
                 Result.success(Unit)
             }  catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    // Function to check if the user's email exists in the public.users table
    override suspend fun isEmailInPublicUsersTable(email: String): Result<Unit> {
        return try {
            postgrest.from("users")
                .select(Columns.list("email")) {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeList<Map<String, String>>()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyEmail(email: String, token: String): Result<Unit> {
        return try {
            auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = email,
                token = token
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val session = auth.currentSessionOrNull()
            val user = auth.retrieveUserForCurrentSession()
            if (session != null && user != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid credentials. Please try again"))
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e.message?.contains("Email not confirmed") == true) {
                // If the email isn’t confirmed, trigger resend.
                auth.resendEmail(OtpType.Email.SIGNUP, email)
                Result.failure(Exception("Email not verified. Verification email sent."))
            } else {
                Result.failure(e)
            }
        }
    }
}
