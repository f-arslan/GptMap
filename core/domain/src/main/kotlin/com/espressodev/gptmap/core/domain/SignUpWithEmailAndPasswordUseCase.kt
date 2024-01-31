package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.model.Exceptions.FirebaseUserIdIsNullException
import com.espressodev.gptmap.core.model.User
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpWithEmailAndPasswordUseCase @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(email: String, password: String, fullName: String): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
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
        }

    private suspend fun saveUserToDatabaseIfUserNotExist(
        authResult: AuthResult,
        email: String,
        fullName: String,
    ) {
        authResult.additionalUserInfo?.isNewUser?.let {
            val id = authResult.user?.uid ?: throw FirebaseUserIdIsNullException()
            val user = User(userId = id, fullName = fullName, email = email)
            firestoreService.saveUser(user)
        } ?: throw FirebaseUserIdIsNullException()
    }
}
