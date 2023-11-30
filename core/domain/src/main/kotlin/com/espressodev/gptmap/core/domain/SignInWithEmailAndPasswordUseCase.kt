package com.espressodev.gptmap.core.domain

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.domain.worker.UpdateDatabaseIfUserEmailVerificationIsFalseWorker
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val accountService: AccountService,
    private val realmAccountService: RealmAccountService,
    private val applicationContext: Context
) {
    suspend operator fun invoke(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val authResult = accountService.firebaseSignInWithEmailAndPassword(email, password)
            accountService.reloadFirebaseUser()

            val isEmailVerified = authResult.user?.isEmailVerified ?: false
            if (!isEmailVerified) throw EmailVerificationIsFalseException()

            updateDatabaseIfUserEmailVerificationFieldIsFalse(authResult)

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

    private fun updateDatabaseIfUserEmailVerificationFieldIsFalse(authResult: AuthResult) {
        authResult.user?.uid?.also { uid ->
            val workRequest =
                OneTimeWorkRequestBuilder<UpdateDatabaseIfUserEmailVerificationIsFalseWorker>()
                    .setInputData(workDataOf(USER_ID to uid))
                    .build()

            WorkManager.getInstance(applicationContext).enqueue(workRequest)
        }
    }


    companion object {
        const val USER_ID = "USER_ID"
    }

}

class EmailVerificationIsFalseException : Exception("Email verification is false")
