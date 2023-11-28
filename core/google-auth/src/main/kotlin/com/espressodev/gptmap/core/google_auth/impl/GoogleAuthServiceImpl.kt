package com.espressodev.gptmap.core.google_auth.impl

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google_auth.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.Provider
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_IN_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleConstants.SIGN_UP_REQUEST
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
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
    private val realmSyncService: RealmSyncService
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
        withContext(Dispatchers.IO) {
            try {
                val authResult = auth.signInWithCredential(googleCredential).await()

                loginToRealm(authResult)

                addUserToDatabase(authResult)

                GoogleResponse.Success(true)
            } catch (e: Exception) {
                GoogleResponse.Failure(e)
            }
        }

    private suspend fun addUserToDatabase(authResult: AuthResult) {
        authResult.additionalUserInfo?.isNewUser?.also {
            if (it)
                authResult.user?.also { user ->
                    addUserToDatabase(user, Provider.GOOGLE).onFailure {
                        throw Exception("Failed to add user to database")
                    }
                }
        }
    }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).onFailure { throwable ->
                throw Exception(throwable)
            }
        }
    }


    private suspend fun addUserToDatabase(
        firebaseUser: FirebaseUser,
        provider: Provider
    ): Result<Boolean> {
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
            return realmSyncService.addUser(user.toRealmUser())
        }
    }

    companion object {
        const val TAG = "GoogleAuthServiceImpl"
    }
}