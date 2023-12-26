package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.Exceptions.FirebaseUserIdIsNullException
import com.espressodev.gptmap.core.Exceptions.FirebaseEmailVerificationIsFalseException
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val accountService: AccountService,
    private val realmAccountService: RealmAccountService,
) {
    suspend operator fun invoke(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val authResult = accountService.firebaseSignInWithEmailAndPassword(email, password)
            accountService.reloadFirebaseUser()

            val isEmailVerified = authResult.user?.isEmailVerified == true
            if (!isEmailVerified) throw FirebaseEmailVerificationIsFalseException()

            loginToRealm(authResult)

            Result.success(value = true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).onFailure { throwable ->
                throw Exception(throwable)
            }
        } ?: throw FirebaseUserIdIsNullException()
    }

}

