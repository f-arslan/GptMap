package com.espressodev.gptmap.core.data.repository.impl

import com.espressodev.gptmap.core.data.di.Dispatcher
import com.espressodev.gptmap.core.data.di.GmDispatchers.IO
import com.espressodev.gptmap.core.data.repository.AuthenticationRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.FirestoreDataStore
import com.espressodev.gptmap.core.google.GoogleAuthService
import com.espressodev.gptmap.core.google.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.Exceptions
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

class AuthenticationRepositoryImpl @Inject constructor(
    private val accountService: AccountService,
    private val firestoreDataStore: FirestoreDataStore,
    private val realmAccountService: RealmAccountService,
    private val googleAuthService: GoogleAuthService,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : AuthenticationRepository {
    override suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse =
        withContext(ioDispatcher) {
            googleAuthService.oneTapSignUpWithGoogle()
        }

    override suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse =
        withContext(ioDispatcher) {
            googleAuthService.oneTapSignInWithGoogle()
        }

    override suspend fun firebaseSignInUpWithGoogle(googleCredential: AuthCredential): SignInUpWithGoogleResponse =
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

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> =
        runCatchingWithContext(ioDispatcher) {
            val authResult =
                accountService.firebaseSignInWithEmailAndPasswordAndReturnResult(email, password)
            accountService.reloadFirebaseUser()

            val isEmailVerified = authResult.user?.isEmailVerified == true
            if (!isEmailVerified) throw Exceptions.FirebaseEmailVerificationIsFalseException()

            loginToRealm(authResult)
        }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        fullName: String
    ): Result<Unit> = runCatchingWithContext(ioDispatcher) {
        val authResult =
            accountService.firebaseSignUpWithEmailAndPassword(
                email = email,
                password = password,
            )
        saveUserToDatabaseIfUserNotExist(
            authResult = authResult,
            email = email,
            fullName = fullName
        )
        accountService.sendEmailVerification()
        Unit
    }

    private suspend fun saveUserToDatabaseIfUserNotExist(
        authResult: AuthResult,
        email: String,
        fullName: String,
    ) {
        authResult.additionalUserInfo?.isNewUser?.let {
            val id = authResult.user?.uid ?: throw Exceptions.FirebaseUserIdIsNullException()
            val user = User(userId = id, fullName = fullName, email = email)
            firestoreDataStore.saveUser(user)
        } ?: throw Exceptions.FirebaseUserIdIsNullException()
    }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).getOrThrow()
        } ?: throw Exceptions.FirebaseUserIdIsNullException()
    }

    private suspend fun addUserToDatabaseIfUserIsNew(authResult: AuthResult) {
        authResult.additionalUserInfo?.isNewUser?.also {
            if (it) {
                authResult.user?.also { user ->
                    addUserToFirestore(user).getOrThrow()
                }
            }
        }
    }

    private suspend fun addUserToFirestore(firebaseUser: FirebaseUser): Result<Boolean> {
        firebaseUser.apply {
            val displayName = displayName ?: throw Exceptions.FirebaseDisplayNameNullException()
            val email = email ?: throw Exceptions.FirebaseEmailNullException()
            val photoUrl = photoUrl ?: throw Exceptions.FirebasePhotoUrlNullException()
            val user =
                User(
                    userId = uid,
                    fullName = displayName,
                    email = email,
                    provider = Provider.GOOGLE.name,
                    profilePictureUrl = photoUrl.toString(),
                )
            firestoreDataStore.saveUser(user)
        }
        return Result.success(value = true)
    }
}
