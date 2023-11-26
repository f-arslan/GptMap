package com.espressodev.gptmap.core.google_auth.impl

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google_auth.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.Provider
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_IN_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_UP_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.RealmSyncServiceImpl
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GoogleAuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private val signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private val signUpRequest: BeginSignInRequest,
    private val firestoreService: FirestoreService,
    private val realmAccountService: RealmAccountService,
) : GoogleAuthService {
    override suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            GoogleResponse.Success(signInResult)
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                GoogleResponse.Success(signUpResult)
            } catch (e: Exception) {
                GoogleResponse.Failure(e)

            }
        }
    }

    override suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse {
        return try {
            val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
            GoogleResponse.Success(signUpResult)
        } catch (e: Exception) {
            GoogleResponse.Failure(e)
        }
    }

    override suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInUpWithGoogleResponse =
        withContext(Dispatchers.Main) {
            try {
                val authResult = auth.signInWithCredential(googleCredential).await()

                val response = authResult?.user?.getIdToken(true)?.await()?.token?.let {
                    realmAccountService.loginWithEmail(it)
                } ?: throw Exception("Google client id didn't got it")
                authResult.additionalUserInfo?.isNewUser?.also {
                    authResult.user?.also { user ->
                        launch {
                            addUserToDatabase(user, Provider.GOOGLE)
                        }
                    }
                }
                if (response is Response.Success) {
                    GoogleResponse.Success(true)
                } else {
                    GoogleResponse.Failure(Exception("Realm auth problem"))
                }
            } catch (e: Exception) {
                GoogleResponse.Failure(e)
            }
        }

    private suspend fun addUserToDatabase(firebaseUser: FirebaseUser, provider: Provider) {
        firebaseUser.apply {
            val displayName = displayName ?: throw Exception("Display name is null")
            val email = email ?: throw Exception("Email is null")
            val photoUrl = photoUrl ?: throw Exception("Photo url is null")
            val user = User(
                userId = uid,
                fullName = displayName,
                isEmailVerified = isEmailVerified,
                email = email,
                provider = provider.name,
                profilePictureUrl = photoUrl.toString()
            )
            firestoreService.saveUser(user)
        }
    }

    companion object {
        const val TAG = "GoogleAuthServiceImpl"
    }
}