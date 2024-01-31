package com.espressodev.gptmap.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteUserFromRealmWorker @AssistedInject constructor(
    @Assisted private val realmSyncService: RealmSyncService,
    @Assisted private val realmAccountService: RealmAccountService,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = try {
        realmSyncService.deleteUser().getOrThrow()
        realmAccountService.revokeAccess()
        Result.success()
    } catch (e: Exception) {
        Log.e("DeleteUserFromRealmWorker", "doWork: failure $e")
        Result.failure()
    }
}
