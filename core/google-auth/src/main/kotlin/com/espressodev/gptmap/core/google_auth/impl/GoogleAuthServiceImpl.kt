package com.espressodev.gptmap.core.google_auth.impl

import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_IN_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_UP_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class GoogleAuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private val signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private val signUpRequest: BeginSignInRequest,
) : GoogleAuthService {

    override suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse {
        return try {
            // Attempt to sign in the user
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            GoogleResponse.Success(signInResult)
        } catch (e: ApiException) {
            if (e.statusCode == CommonStatusCodes.SIGN_IN_REQUIRED) {
                try {
                    // Attempt to sign up the user
                    val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                    GoogleResponse.Success(signUpResult)
                } catch (signUpException: ApiException) {
                    GoogleResponse.Failure(signUpException)
                } catch (signUpException: CancellationException) {
                    GoogleResponse.Failure(signUpException)
                } catch (signUpException: IOException) {
                    GoogleResponse.Failure(signUpException)
                }
            } else {
                GoogleResponse.Failure(e)
            }
        } catch (e: CancellationException) {
            GoogleResponse.Failure(e)
        } catch (e: IOException) {
            GoogleResponse.Failure(e)
        }
    }

    override suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse {
        return try {
            val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
            GoogleResponse.Success(signUpResult)
        } catch (e: ApiException) {
            GoogleResponse.Failure(e)
        } catch (e: CancellationException) {
            GoogleResponse.Failure(e)
        } catch (e: IOException) {
            GoogleResponse.Failure(e)
        }
    }

    override suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): AuthResult =
        auth.signInWithCredential(googleCredential).await()
}
