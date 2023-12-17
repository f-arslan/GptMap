package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

typealias SignUpResponse = Result<Boolean>

class SignUpWithEmailAndPasswordUseCase @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val realmSyncService: RealmSyncService
) {
    suspend operator fun invoke(email: String, password: String, fullName: String): SignUpResponse =
        withContext(Dispatchers.IO) {
            try {
                val authResult =
                    accountService.firebaseSignUpWithEmailAndPassword(email, password, fullName)

                saveUserToDatabaseIfUserNotExist(authResult, email, fullName)

                accountService.sendEmailVerification()

                Result.success(value = true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private suspend fun saveUserToDatabaseIfUserNotExist(
        authResult: AuthResult,
        email: String,
        fullName: String
    ) {
        authResult.user?.uid?.let { userId ->
            val user = User(userId = userId, fullName = fullName, email = email)
            firestoreService.saveUser(user)
            realmSyncService.saveUser(user.toRealmUser())
        } ?: throw UserIdIsNullException()
    }

    companion object {
        class UserIdIsNullException : Exception("User id is null")
    }
}
