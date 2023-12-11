package com.espressodev.gptmap.core.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.domain.SignInWithEmailAndPasswordUseCase.Companion.USER_ID
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateDatabaseIfUserEmailVerificationIsFalseWorker @AssistedInject constructor(
    @Assisted private val firestoreService: FirestoreService,
    @Assisted private val realmSyncService: RealmSyncService,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = try {
        val userId = inputData.getString(USER_ID) ?: throw Exception("User id is null")
        firestoreService.getUser(userId).onSuccess { user ->
            if (!user.isEmailVerified) {
                firestoreService.updateUserEmailVerification(userId, true)
                    .onFailure {
                        throw FailedToUpdateUserEmailVerificationException()
                    }
                realmSyncService.addUser(user.copy(isEmailVerified = true).toRealmUser())
            }
        }.onFailure {
            throw FailedToGetUserException()
        }
        Result.success()
    } catch (e: Exception) {
        Result.failure()
    }
}


class FailedToGetUserException : Exception("Failed to get user")
class FailedToUpdateUserEmailVerificationException :
    Exception("Failed to update user email verification")