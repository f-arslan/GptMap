package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google_auth.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.Exceptions.FirebaseDisplayNameNullException
import com.espressodev.gptmap.core.model.Exceptions.FirebaseEmailNullException
import com.espressodev.gptmap.core.model.Exceptions.FirebasePhotoUrlNullException
import com.espressodev.gptmap.core.model.Exceptions.FirebaseUserIdIsNullException
import com.espressodev.gptmap.core.model.Provider
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInUpWithGoogleUseCase @Inject constructor(
    private val googleAuthService: GoogleAuthService,
    private val realmAccountService: RealmAccountService,
    private val firestoreService: FirestoreService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse =
        withContext(ioDispatcher) {
            googleAuthService.oneTapSignUpWithGoogle()
        }

    suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse =
        withContext(ioDispatcher) {
            googleAuthService.oneTapSignInWithGoogle()
        }

    suspend fun firebaseSignInUpWithGoogle(googleCredential: AuthCredential): SignInUpWithGoogleResponse =
        withContext(ioDispatcher) {
            try {
                val authResult = googleAuthService.firebaseSignInWithGoogle(googleCredential)

                loginToRealm(authResult)

                addUserToDatabaseIfUserIsNew(authResult)

                GoogleResponse.Success(data = true)
            } catch (e: Exception) {
                GoogleResponse.Failure(e)
            }
        }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).getOrThrow()
        } ?: throw FirebaseUserIdIsNullException()
    }

    private fun addUserToDatabaseIfUserIsNew(authResult: AuthResult) {
        authResult.additionalUserInfo?.isNewUser?.also {
            if (it) {
                authResult.user?.also { user ->
                    addUserToFirestore(user).getOrThrow()
                }
            }
        }
    }

    private fun addUserToFirestore(firebaseUser: FirebaseUser): Result<Boolean> {
        firebaseUser.apply {
            val displayName = displayName ?: throw FirebaseDisplayNameNullException()
            val email = email ?: throw FirebaseEmailNullException()
            val photoUrl = photoUrl ?: throw FirebasePhotoUrlNullException()
            val user =
                User(
                    userId = uid,
                    fullName = displayName,
                    email = email,
                    provider = Provider.GOOGLE.name,
                    profilePictureUrl = photoUrl.toString(),
                )
            firestoreService.saveUser(user)
        }
        return Result.success(value = true)
    }
}
