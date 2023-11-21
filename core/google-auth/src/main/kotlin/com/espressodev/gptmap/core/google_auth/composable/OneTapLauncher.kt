package com.espressodev.gptmap.core.google_auth.composable

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.espressodev.gptmap.core.designsystem.component.GmCircularIndicator
import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google_auth.SignInUpWithGoogleResponse
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Job
import com.espressodev.gptmap.core.model.google.GoogleResponse.Loading
import com.espressodev.gptmap.core.model.google.GoogleResponse.Success
import com.espressodev.gptmap.core.model.google.GoogleResponse.Failure

@Composable
fun OneTapLauncher(
    oneTapClient: SignInClient,
    oneTapSignInUpResponse: OneTapSignInUpResponse,
    singInUpWithGoogleResponse: SignInUpWithGoogleResponse,
    signInWithGoogle: (AuthCredential) -> Job,
    navigate: () -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credentials =
                        oneTapClient.getSignInCredentialFromIntent(result.data)
                    Log.d("OneTapLauncher", "credentials: $credentials")
                    val googleIdToken = credentials.googleIdToken
                    val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                    signInWithGoogle(googleCredentials)
                } catch (it: ApiException) {
                    Log.d("OneTapLauncher", "ApiException: $it")
                    print(it)
                }
            }
        }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        Log.d("OneTapLauncher", "intent: $intent")
        launcher.launch(intent)
    }

    OneTapSignInUp(oneTapSignInUpResponse, launch = { launch(it) })

    SignInUpWithGoogle(singInUpWithGoogleResponse, navigateToHomeScreen = { signedIn ->
        if (signedIn) {
            navigate()
        }
    })
}

@Composable
fun OneTapSignInUp(
    oneTapSignUpResponse: OneTapSignInUpResponse,
    launch: (result: BeginSignInResult) -> Unit
) {
    when (oneTapSignUpResponse) {
        is Loading -> GmCircularIndicator()
        is Success -> oneTapSignUpResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }

        is Failure -> LaunchedEffect(Unit) {
            Log.d("OneTapSignUp", oneTapSignUpResponse.e.toString())
        }
    }
}

@Composable
fun SignInUpWithGoogle(
    signUpWithGoogleResponse: SignInUpWithGoogleResponse,
    navigateToHomeScreen: (signedIn: Boolean) -> Unit
) {
    when (signUpWithGoogleResponse) {
        is Loading -> GmCircularIndicator()
        is Success -> signUpWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToHomeScreen(signedIn)
            }
        }

        is Failure -> LaunchedEffect(Unit) {
            Log.d("SignUpWithGoogle", signUpWithGoogleResponse.e.toString())
        }
    }
}