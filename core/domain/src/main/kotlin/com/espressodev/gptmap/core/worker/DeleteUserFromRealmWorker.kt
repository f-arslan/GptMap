package com.espressodev.gptmap.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService

@HiltWorker
class DeleteUserFromRealmWorker(
    private val realmSyncService: RealmSyncService,
    private val realmAccountService: RealmAccountService,
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = try {
        realmSyncService.deleteUser().getOrThrow()
        realmAccountService.revokeAccess()
        Result.success()
    } catch (e: Exception) {
        Log.e("DeleteUserFromRealmWorker", "doWork: failure $e")
        Result.failure()
    }

    class Factory(
        private val realmSyncService: RealmSyncService,
        private val realmAccountService: RealmAccountService
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ) = if (workerClassName == DeleteUserFromRealmWorker::class.java.name) {
            DeleteUserFromRealmWorker(
                realmSyncService,
                realmAccountService,
                appContext,
                workerParameters
            )
        } else {
            null
        }
    }
}
