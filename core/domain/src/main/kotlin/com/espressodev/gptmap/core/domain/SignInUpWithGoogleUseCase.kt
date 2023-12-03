package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google_auth.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.Provider
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInUpWithGoogleUseCase @Inject constructor(
    private val googleAuthService: GoogleAuthService,
    private val realmAccountService: RealmAccountService,
    private val realmSyncService: RealmSyncService,
    private val firestoreService: FirestoreService,
) {

    suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse = withContext(Dispatchers.IO) {
        googleAuthService.oneTapSignUpWithGoogle()
    }

    suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse = withContext(Dispatchers.IO) {
        googleAuthService.oneTapSignInWithGoogle()
    }

    suspend fun firebaseSignInUpWithGoogle(googleCredential: AuthCredential): SignInUpWithGoogleResponse =
        withContext(Dispatchers.IO) {
            try {
                val authResult = googleAuthService.firebaseSignInWithGoogle(googleCredential)

                loginToRealm(authResult)

                addUserToDatabaseIfUserIsNew(authResult)

                GoogleResponse.Success(true)
            } catch (e: Exception) {
                GoogleResponse.Failure(e)
            }
        }


    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).onFailure { throwable ->
                throw Exception(throwable)
            }
        }
    }

    private suspend fun addUserToDatabaseIfUserIsNew(authResult: AuthResult) {
        authResult.additionalUserInfo?.isNewUser?.also {
            if (it)
                authResult.user?.also { user ->
                    addUserToDatabase(user, Provider.GOOGLE).onFailure {
                        throw Exception("Failed to add user to database")
                    }
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
}