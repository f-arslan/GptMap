package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.model.Exceptions.FirebaseEmailVerificationIsFalseException
import com.espressodev.gptmap.core.model.Exceptions.FirebaseUserIdIsNullException
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val accountService: AccountService,
    private val realmAccountService: RealmAccountService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(email: String, password: String) = withContext(ioDispatcher) {
        runCatching {
            val authResult = accountService.firebaseSignInWithEmailAndPassword(email, password)
            accountService.reloadFirebaseUser()

            val isEmailVerified = authResult.user?.isEmailVerified == true
            if (!isEmailVerified) throw FirebaseEmailVerificationIsFalseException()

            loginToRealm(authResult)

            true
        }
    }

    private suspend fun loginToRealm(authResult: AuthResult) {
        authResult.user?.getIdToken(true)?.await()?.token?.let {
            realmAccountService.loginWithEmail(it).getOrThrow()
        } ?: throw FirebaseUserIdIsNullException()
    }
}
