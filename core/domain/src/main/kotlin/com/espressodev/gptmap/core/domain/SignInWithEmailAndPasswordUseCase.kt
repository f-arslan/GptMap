package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val realmSyncService: RealmSyncService,
    private val realmAccountService: RealmAccountService
) {

    suspend operator fun invoke(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val authResult = accountService.firebaseSignInWithEmailAndPassword(email, password)

            val isEmailVerified = authResult.user?.isEmailVerified ?: false

            if (isEmailVerified)
                updateUserIfEmailVerificationFieldIsFalse(authResult)

            loginToRealm(authResult)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).onFailure { throwable ->
                throw Exception(throwable)
            }
        }
    }

    private suspend fun updateUserIfEmailVerificationFieldIsFalse(authResult: AuthResult) {
         authResult.user?.uid?.also { uid ->
             firestoreService.getUser(uid).onSuccess { user ->
                 if (!user.isEmailVerified) {
                     firestoreService.updateUserEmailVerification(uid, true).onFailure {
                         throw FailedToUpdateUserEmailVerificationException()
                     }
                 }
                 realmSyncService.addUser(user.copy(isEmailVerified = true).toRealmUser())
             }.onFailure {
                 throw FailedToGetUserException()
             }
         }
    }

}

class FailedToGetUserException : Exception("Failed to get user")
class FailedToUpdateUserEmailVerificationException :
    Exception("Failed to update user email verification")
