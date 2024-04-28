package com.espressodev.gptmap.core.data.repository.impl

import android.content.Context
import com.espressodev.gptmap.core.data.repository.AuthenticationRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.FirestoreRepository
import com.espressodev.gptmap.core.google.GoogleAuthService
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.firebase.Provider
import com.espressodev.gptmap.core.model.firebase.User
import com.espressodev.gptmap.core.model.google.AuthState
import com.espressodev.gptmap.core.mongodb.RealmAccountRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val accountService: AccountService,
    private val firestoreRepository: FirestoreRepository,
    private val realmAccountRepository: RealmAccountRepository,
    private val googleAuthService: GoogleAuthService,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : AuthenticationRepository {
    override fun signInUpWithGoogle(context: Context): Flow<AuthState<Unit>> = flow {
        googleAuthService.googleSignInUp(context).collect { state ->
            when (state) {
                is AuthState.Success -> {
                    try {
                        loginToRealm(state.data)
                        addUserToDatabaseIfUserIsNew(state.data)
                        emit(AuthState.Success(Unit))
                    } catch (e: Exception) {
                        emit(AuthState.Error(e))
                    }
                }

                is AuthState.Error -> emit(AuthState.Error(state.e))
                AuthState.Loading -> emit(AuthState.Loading)
                AuthState.Idle -> emit(AuthState.Idle)
            }
        }
    }.flowOn(ioDispatcher)

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
            firestoreRepository.saveUser(user)
        } ?: throw Exceptions.FirebaseUserIdIsNullException()
    }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountRepository.loginWithEmail(it).getOrThrow()
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
            firestoreRepository.saveUser(user)
        }
        return Result.success(value = true)
    }
}
